package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Common.Mail;
import Common.ObjectMessage;
import Common.Report;

public abstract class ADailyDBController 
{
	final static String userName = "OBLManager2019@gmail.com";
	final static String password = "Aa112233";
	
    static ScheduledThreadPoolExecutor executor;
    static ExecutorService pool = Executors.newFixedThreadPool(5); 
    
    public static void startThreads(Connection connToSQL)
    {
    	executor=new ScheduledThreadPoolExecutor(10);
    	executor.scheduleAtFixedRate(() -> resetUserStatusHistory(connToSQL), 0, 1, TimeUnit.DAYS);
    }
    
    
	public static void selection(ObjectMessage msg, Connection connToSQL)
	{

		if (((msg.getMessage()).equals("sendMail")))
		{
			pool.execute(()->sendMail(msg, connToSQL));
		}
	}

	
	
	private static void sendMail(ObjectMessage msg, Connection connToSQL) 
	{
			Mail mail=((Mail)((ObjectMessage)msg).getObjectList().get(0));
			String[] to = { mail.getTo() }; // list of recipient email addresses
	        String subject = mail.getSubject();
	        String body = mail.getBody();
		    Properties props = System.getProperties();
	        String host = "smtp.gmail.com";
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.password", password);
	        props.put("mail.smtp.port", "587");
	        props.put("mail.smtp.auth", "true");

	        Session session = Session.getInstance(props);
	        MimeMessage message;
	        try 
	        {
	        	message = new MimeMessage(session);
	        }
	        catch (Exception me) 
	        {
	            me.printStackTrace();
	            message = null;
	        }
	        try 
	        {
	            message.setFrom(new InternetAddress(userName));
	            InternetAddress[] toAddress = new InternetAddress[to.length];

	            // To get the array of addresses
	            for( int i = 0; i < to.length; i++ ) 
	            {
	                toAddress[i] = new InternetAddress(to[i]);
	            }

	            for( int i = 0; i < toAddress.length; i++) 
	            {
	                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	            }

	            message.setSubject(subject);
	            message.setText(body);
	            Transport transport = session.getTransport("smtp");
	            transport.connect(host, userName, password);
	            transport.sendMessage(message, message.getAllRecipients());
	            transport.close();
	        }
	        catch (AddressException ae) {
	            ae.printStackTrace();
	        }
	        catch (MessagingException me) {
	            me.printStackTrace();
	        }
	}
	
	//check the methode 
	private static void monthlyUpdateUserStatusHistory(Connection connToSQL)
	{
		PreparedStatement ps=null;
		ResultSet query1 = null;
		PreparedStatement ps2=null;
		try 
		{
			ps=connToSQL.prepareStatement("SELECT ID, status FROM readeraccount");
			query1=ps.executeQuery();
			while(query1.next())
			{
				ps2=connToSQL.prepareStatement("INSERT INTO `UserStatusHistory` (`readerAccountID`,`status`) VALUES (?,?)");
				ps2.setString(1, query1.getString(1));
				ps2.setString(2, query1.getString(2));
				ps2.executeUpdate();
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
		
	}

	
	private static void  resetUserStatusHistory(Connection connToSQL)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd");
		SimpleDateFormat format2 = new SimpleDateFormat("MM");
		Date now =  Calendar.getInstance().getTime();
   	 	String today=format.format(now);
   	 	if(today.equals("01"))
   	 	{
   	 		PreparedStatement ps,ps2;
   	 		try 
   	 		{
   	 			updateHistoryStatusReader(connToSQL,Integer.parseInt(format2.format(now)));
   	 			ps= connToSQL.prepareStatement("DROP TABLE IF EXISTS `UserStatusHistory`");
				ps2= connToSQL.prepareStatement("CREATE TABLE `UserStatusHistory` (\r\n" + 
						"  `readerAccountID` varchar(9) NOT NULL ,\r\n" + 
						"  `status` enum('Active','Frozen','Locked')  NOT NULL,\r\n" + 
						"  PRIMARY KEY  (`readerAccountID`,`status`),\r\n" + 
						"  FOREIGN KEY (`readerAccountID`) REFERENCES `ReaderAccount` (`ID`)\r\n" + 
						") ENGINE=InnoDB;");
				ps.executeUpdate();
				ps2.executeUpdate();
				monthlyUpdateUserStatusHistory(connToSQL);
				
   	 		} 
   	 		catch (SQLException e) 
   	 		{
   	 			e.printStackTrace();
   	 		}
   	 	}	
	}

	
	private static void  updateHistoryStatusReader(Connection connToSQL,int month)
	{
		PreparedStatement ps;
		ResultSet rs;
		Report members=new Report();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
		String today=sdf.format(date);
		try 
		{
			ps = connToSQL.prepareStatement("SELECT COUNT(*) FROM UserStatusHistory WHERE status=?"); 
			ps.setString(1, "Active");
			rs =ps.executeQuery();
			rs.next();
			members.setActiveReaderAccounts(Integer.toString(rs.getInt(1)));
			ps.setString(1, "Frozen");
			rs =ps.executeQuery();
			rs.next();
			members.setFrozenReaderAccounts(Integer.toString(rs.getInt(1)));
			ps.setString(1, "Locked");
			rs =ps.executeQuery();
			rs.next();
			members.setLockedReaderAccounts(Integer.toString(rs.getInt(1)));
			ps=connToSQL.prepareStatement("INSERT INTO `History` (`action`,`date`,`Note`) VALUES ('Active readerAccounts',?,?); "); 
			ps.setString(1, today);
			ps.setString(2, members.getActiveReaderAccounts());
			ps.executeUpdate();
			ps=connToSQL.prepareStatement("INSERT INTO `History` (`action`,`date`,`Note`) VALUES ('Frozen readerAccounts',?,?); "); 
			ps.setString(1, today);
			ps.setString(2, members.getFrozenReaderAccounts());
			ps.executeUpdate();
			ps=connToSQL.prepareStatement("INSERT INTO `History` (`action`,`date`,`Note`) VALUES ('Locked readerAccounts',?,?); "); 
			ps.setString(1, today);
			ps.setString(2, members.getLockedReaderAccounts());
			ps.executeUpdate();
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}

