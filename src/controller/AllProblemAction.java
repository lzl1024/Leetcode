package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.Model;
import model.ProblemDAO;

import org.genericdao.RollbackException;

import databeans.Problem;

public class AllProblemAction extends Action {

	private ProblemDAO  problemDAO;

    public AllProblemAction(Model model) {
    	problemDAO = model.getProblemDAO();
	}

    public String getName() { return "allproblem.do"; }

    public String perform(HttpServletRequest request) {
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
			Problem[] problems = problemDAO.match();
			Arrays.sort(problems);
			String begin;
			if((begin = request.getParameter("begin")) == null) {
				request.setAttribute("begin",1);
			}else {
				int b = Integer.parseInt(begin);
				if (b <= 0) b = 1;
				request.setAttribute("begin", b);
			}
			request.setAttribute("problemlist",problems);
			
			Problem[] hotProblem = problemDAO.match();
			Problem p = new Problem();
			Arrays.sort(hotProblem, p.hp);
			if (hotProblem.length > 10)
				hotProblem = Arrays.copyOf(hotProblem, 10);
			
			//System.out.println(hotProblem.length);
			request.setAttribute("hotproblem", hotProblem);
			
			
	        return "problemList.jsp";
		}catch (NumberFormatException e) {
    		errors.add(e.getMessage());
    		return "error.jsp";
        } catch (RollbackException e) {
        	errors.add(e.getMessage());
        	return "error.jsp";
        }
    }
}