package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import DBConnection.DBHandler;
import javafx.scene.control.Alert;

public class Email {
	protected static String generateTemporaryPassword = "";
	private static DBHandler handler;

	protected static boolean sendResetEmail(String userEmail, String temporaryPassword) {
		String host = "smtp.gmail.com";
		String username = "teamcarreview@gmail.com";
		String password = "ayuw fntl hbec mjpc";
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
			message.setSubject("Password Reset");
			message.setText("Your reset code is: " + temporaryPassword);
			Transport.send(message);
			return true;
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	static void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	protected static String getEmail(String targetUsername) {
		handler = new DBHandler();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = handler.getConnection();
			String query = "SELECT email FROM customer WHERE customerId = ?";
			String query1 = "SELECT email FROM employee WHERE employeeId = ?";
			if (LoginController.getCustomer() == null)
				preparedStatement = connection.prepareStatement(query1);
			else
				preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, targetUsername);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String userEmail = resultSet.getString("email");
				System.out.println("Email for username '" + targetUsername + "': " + userEmail);
				return userEmail;
			} else {
				System.out.println("Username not found.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}

	boolean sendCode(String userEmail) {
		userEmail = getEmail(LoginController.usr);
		generateTemporaryPassword = GenerateCode();
		if (sendResetEmail(userEmail, generateTemporaryPassword)) {
			showAlert("Success", "A temporary password has been sent to your email (" + userEmail + ").");
			return true;
		} else {
			showAlert("Error", "Failed to send the reset email. Please try again.");
		}
		return false;
	}

	protected String GenerateCode() {
		int min = 100000;
		int max = 999999;
		Random random = new Random();
		int code = random.nextInt(max - min + 1) + min;
		return String.valueOf(code);
	}
}
