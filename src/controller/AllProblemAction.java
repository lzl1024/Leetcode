package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.BlogDAO;
import model.Model;
import model.ProblemDAO;

import org.genericdao.RollbackException;

import databeans.Blog;
import databeans.Problem;

public class AllProblemAction extends Action {

	private ProblemDAO  problemDAO;

    public AllProblemAction(Model model) {
    	problemDAO = model.getProblemDAO();
	}

    public String getName() { return "allproblem.do"; }

    public String perform(HttpServletRequest request) {
        // Set up the request attributes (the errors list and the form bean so
        // we can just return to the jsp with the form if the request isn't correct)
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
			Problem[] problems = problemDAO.match();
			Arrays.sort(problems);
			String begin;
			if((begin = request.getParameter("begin")) == null) {
				request.setAttribute("begin",1);
			}else {
				request.setAttribute("begin", Integer.parseInt(begin));
			}
			request.setAttribute("problemlist",problems);
			
	        return "problemList.jsp";
        } catch (RollbackException e) {
        	errors.add(e.getMessage());
        	return "error.jsp";
        }
    }
}