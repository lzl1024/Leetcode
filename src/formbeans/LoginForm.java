package formbeans;

import java.util.ArrayList;
import java.util.List;

import org.mybeans.form.FormBean;

public class LoginForm extends FormBean {
	private String email;
	private String password;
	
	public String getEmail()  { return email; }
	public String getPassword()  { return password; }
	
	public void setEmail(String s) { email = trimAndConvert(s,"<>\"");  }
	public void setPassword(String s) {	password = s.trim();                  }

	public List<String> getValidationErrors() {
		List<String> errors = new ArrayList<String>();
		
		if (email == null || email.length() == 0) {
			errors.add("Email is required");
		}
		
		if (email.length() > 255) {
			errors.add("Please edit email less than 255 characters!");
		}
		
		if (password == null || password.length() == 0) {
			errors.add("Password is required");
		}
		
		return errors;
	}
}