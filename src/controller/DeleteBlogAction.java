package controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.BlogDAO;
import model.CommentDAO;
import model.Model;

import org.genericdao.MatchArg;
import org.genericdao.RollbackException;
import org.genericdao.Transaction;

import databeans.Blog;
import databeans.Comment;
import databeans.User;

public class DeleteBlogAction extends Action {
	
	private BlogDAO	blogDAO;
	private CommentDAO commentDAO;

	public DeleteBlogAction(Model model) {
		blogDAO = model.getBlogDAO();
		commentDAO = model.getCommentDAO();
	}

	public String getName() { return "deleteblog.do"; }
    
    public String perform(HttpServletRequest request) {
    	// Set up the errors list
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
			Transaction.begin();
			String strid = request.getParameter("blogid");
			User user = (User) request.getSession(false).getAttribute("user");
			int blogid = Integer.parseInt(strid);
			Blog blog = blogDAO.read(blogid);
			if(blog == null ) {
				errors.add("No blog with id="+blogid);
				return "error.jsp";
			}

    		if (!user.getEmail().equals("admin@admin") && !blog.getEmail().equals(user.getEmail())) {
    			errors.add("Blog with id="+blogid + " is not yours!");
    			return "error.jsp";
    		}
    		
	        blogDAO.delete(blogid);
	        
	        
	        Comment[] comments = commentDAO.match(MatchArg.equals("blogid", blogid));

	        for(Comment comment: comments) {
	        	commentDAO.delete(comment.getId());
	        }
	        Transaction.commit();		
	        
			
			return "yourblog.do";
	 	} catch (RollbackException e) {
			errors.add(e.getMessage());
			return "error.jsp";
		}catch (NumberFormatException e) {
    		errors.add(e.getMessage());
    		return "error.jsp";
    	}finally {
			if (Transaction.isActive()) Transaction.rollback();
		}		
    }
    
}