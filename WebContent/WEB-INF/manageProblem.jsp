<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="template-head.jsp" />

<script>
<!--
function deleteproblem(problemid) {	
	var r=confirm("Are you sure to delte this problem?");
	if (r==true)
	  {
		window.location.assign("deleteproblem.do?problemid="+problemid);
	  }
	
}
</script>

<div class="row-fluid">
<jsp:include page="template-problem-nav.jsp" />

        
<div class="span9"  style="word-wrap: break-word;  
          word-break: normal; ">
<p>
	<table class="ContentTable">
	<tr><td>Title</td>
		<td>Date</td>
		<td>Operations</td>	
	</tr>
<% 	databeans.Problem[] problems = (databeans.Problem[])request.getAttribute("problemlist");
		int begin = (Integer)request.getAttribute("begin");
        for (int i=(begin-1)*10; i<begin*10 && i < problems.length; i++) { 
		%>
		<tr>
			<td><a href="problem.do?id=<%=problems[i].getId()%>"><%=problems[i].getTitle()%></a></td>
			<td><%=problems[i].getDate()%></td>
			<td><a onclick="deleteproblem(<%=problems[i].getId()%>)" href="javascript::deleteproblem(<%=problems[i].getId()%>)">Delete</a>
			&nbsp &nbsp <a href="editproblem.do?id=<%=problems[i].getId()%>">Edit</a></td>
		</tr>
	<%}%>
	</table>
	<div class="pagination pagination-centered">
		<ul>
		<li><a href="manageProblem.do?begin=<%= begin >1 ? begin-1:1%>">Prev</a></li>
		<%  int k = (problems.length-1)/10+1;
        	for (Integer i= 1; i<=k; i++)
        	if (i!=begin){%>
        		<li><a href="manageProblem.do?begin=<%=i%>"><%=i%></a></li>
        	<%} else{%>
        		<li class="disabled"><a href="manageProblem.do?begin=<%=i%>"><%=i%></a></li>
        	<%}%>
        <li><a href="manageProblem.do?begin=<%= begin<k ? begin+1:k%>">Next</a></li>
        </ul>
	</div>
	
		
</div>
	<a class="btn pull-right" href="postProblem.do">Post Problem &raquo;</a>
</div>

<script>
	$(document).ready(function() {
		$("#oj").addClass("active");
		$("#manageproblem").addClass("active");
	});
</script>

<jsp:include page="template-bottom.jsp" />