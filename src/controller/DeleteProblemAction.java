package controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.Model;
import model.ProblemDAO;

import org.genericdao.RollbackException;
import org.genericdao.Transaction;

public class DeleteProblemAction extends Action {
	
	private ProblemDAO	problemDAO;
	//private CommentDAO commentDAO;

	public DeleteProblemAction(Model model) {
		problemDAO = model.getProblemDAO();
		//commentDAO = model.getCommentDAO();
	}

	public String getName() { return "deleteproblem.do"; }
    
    public String perform(HttpServletRequest request) {
    	// Set up the errors list 
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
			String strid = request.getParameter("problemid");
			int problemid = Integer.parseInt(strid);
			System.out.println("problemid: "+ problemid);
			//Transaction.begin();
			//System.out.println("Reach after begin!");
	        problemDAO.delete(problemid);
	        /*
	        //Comment[] comments = commentDAO.match(MatchArg.equals("blogid", blogid));

	        //for(Comment comment: comments) {
	        	//commentDAO.delete(comment.getId());
	        //}
	         * 
	         */
	        //Transaction.commit();	
			System.out.println("Reach after commit!");
			return "manageProblem.do";
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