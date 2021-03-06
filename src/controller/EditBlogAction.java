package controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import model.BlogDAO;
import model.CommentDAO;
import model.Model;
import model.UserDAO;

import org.genericdao.RollbackException;
import org.genericdao.Transaction;
import org.mybeans.form.FormBeanException;
import org.mybeans.form.FormBeanFactory;

import databeans.Blog;
import databeans.Comment;
import databeans.User;

import formbeans.PostBlogForm;

public class EditBlogAction extends Action {
	private FormBeanFactory<PostBlogForm> formBeanFactory = FormBeanFactory.getInstance(PostBlogForm.class);
	

	private BlogDAO  blogDAO;
	private CommentDAO commentDAO;
	private UserDAO userDAO;
	
    public EditBlogAction(Model model) {
    	blogDAO  = model.getBlogDAO();
    	commentDAO = model.getCommentDAO();
    	userDAO = model.getUserDAO();
	}

    public String getName() { return "editblog.do"; }

    public String perform(HttpServletRequest request) {
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
			PostBlogForm form = formBeanFactory.create(request);
			User user = (User) request.getSession(false).getAttribute("user");
		    
    		int id = Integer.parseInt((String)request.getParameter("id"));
    		Blog p = blogDAO.read(id);
    		if (p == null) {
    			errors.add("No blog with id="+id);
    			return "error.jsp";
    		}
    		if (!user.getEmail().equals("admin@admin") && !p.getEmail().equals(user.getEmail())) {
    			errors.add("Blog with id="+id + " is not yours!");
    			return "error.jsp";
    		}
    		
	        Blog[] hotBlog = blogDAO.match();
			Blog b = new Blog();
			Arrays.sort(hotBlog, b.hb);
			if (hotBlog.length > 10)
				hotBlog = Arrays.copyOf(hotBlog, 10);
			
			request.setAttribute("hotblog", hotBlog);
    		
    		PostBlogForm form2 = new PostBlogForm();

    		form2.setContent(p.getReadableCon().replaceAll("<br>", "\n"));
    		form2.setTitle(p.getTitle());
	        // Any validation errors?
	        if (!form.isPresent()) {	        	
	    		request.setAttribute("form",form2);  
	    		request.setAttribute("edit", 1);
	    		request.setAttribute("id", id);
	            return "blog.jsp";
	        }
	         
	       
	        errors.addAll(form.getValidationErrors());
	        if (errors.size() > 0) {
	        	request.setAttribute("form",form2); 
	        	request.setAttribute("edit", 1);
	        	request.setAttribute("id", id);
	        	return "blog.jsp";
	        }
    		
    		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
			Date curDate = new Date(System.currentTimeMillis()); 
			Transaction.begin();
			p.setDate(formatter.format(curDate));
			p.setContent((fixBadChars(form.getContent())).getBytes("Unicode"));
			
			p.setTitle(fixBadChars(form.getTitle()));
			blogDAO.update(p);
			Transaction.commit();
			
			User u = userDAO.read(p.getEmail());
			Blog[] archives = blogDAO.getBlogs(u.getEmail());
  
    		request.setAttribute("blogOwner",u); 
    		request.setAttribute("archives",archives);		
			
    		request.setAttribute("blog",p);  
			Comment[] comments = commentDAO.getComments(p.getId());
			request.setAttribute("commentlist",comments);
    		
            return "viewblog.jsp";
    	} catch (RollbackException e) {
    		errors.add(e.getMessage());
    		return "error.jsp";
    	} catch (FormBeanException e) {
    		errors.add(e.getMessage());
    		return "error.jsp";
    	}catch (NumberFormatException e) {
    		errors.add(e.getMessage());
    		return "error.jsp";
    	}catch (UnsupportedEncodingException e) {
    		errors.add(e.getMessage());
			return "error.jsp";
		}finally {
			if (Transaction.isActive()) Transaction.rollback();
		}
    }
		
		private String fixBadChars(String s) {
			if (s == null || s.length() == 0) return s;
			
			Pattern p = Pattern.compile("[<>\"&]");
	        Matcher m = p.matcher(s);
	        StringBuffer b = null;
	        while (m.find()) {
	            if (b == null) b = new StringBuffer();
	            switch (s.charAt(m.start())) {
	                case '<':  m.appendReplacement(b,"&lt;");
	                           break;
	                case '>':  m.appendReplacement(b,"&gt;");
	                           break;
	                case '&':  m.appendReplacement(b,"&amp;");
	                		   break;
	                case '"':  m.appendReplacement(b,"&quot;");
	                           break;
	                default:   m.appendReplacement(b,"&#"+((int)s.charAt(m.start()))+';');
	            }
	        }
	        
	        if (b == null) return s;
	        m.appendTail(b);
	        return b.toString();
	    }
}