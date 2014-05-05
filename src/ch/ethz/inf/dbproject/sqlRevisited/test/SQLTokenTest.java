package ch.ethz.inf.dbproject.sqlRevisited.test;

import static org.junit.Assert.*;
import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.*;

public class SQLTokenTest {

	private static final String casesForCategoryQuery = "select CaseDetail.* from CaseDetail caseDetail, CategoryForCase categoryForCase where categoryName = ? and caseDetail.caseId = categoryForCase.caseId";
	private static final String getPersonsForConvictionTypeString = "select person.* " +
			   "from Conviction conviction, Person person, CategoryForCase categoryForCase "+
			   "where conviction.personId = person.personId "+
			   "and conviction.caseId = categoryForCase.caseId " +
			   "and categoryForCase.categoryName = ? " +
			   "order by lastName, firstName";
	private static final String addPersonNoteString = "insert into PersonNote(PersonId, PersonNoteId, text, date, authorUsername) values(?, ?, ?, ?, ?)";
	private static final String deleteSuspectFromCaseQuery = "delete from Suspected where caseId=? and personId=?";
	private static final String categoriesForCaseQuery = "select distinct Category.* " +
			"from CaseDetail caseDetail, CategoryForCase categoryForCase, Category category " +
		    "where caseDetail.caseId = ? and categoryForCase.caseId = caseDetail.caseId and categoryForCase.categoryName = category.Name";
	private static final String getMaxPersonNoteIdForPersonIdString = "select max(personNoteId) from PersonNote where personId = ?";
	private static final String getPersonNotesForPersonString = "select * from PersonNote where personId= ? order by date desc";
	private static final String updateCaseIsOpenQuery = "update CaseDetail set isOpen =? where caseId=?";
	private static final String convictionsPerCategoryString = "select categoryName, count(*) " +
		    "from Conviction conviction, CategoryForCase categoryForCase " +
		    "where conviction.caseId = categoryForCase.caseId " +
		    "group by categoryName";
	private static final String oldestUnresolvedCasesQuery = "select * from CaseDetail where isOpen = true order by date asc";

	private static final String literalTest = "select * from CaseDetail where caseId=3 and authorUsername='holmes'";
	
	@Test
	public void test() {
		SQLLexer lex = new SQLLexer();

		System.out.println(lex.tokenize(casesForCategoryQuery));
		System.out.println(lex.tokenize(getPersonsForConvictionTypeString));
		System.out.println(lex.tokenize(addPersonNoteString));
		System.out.println(lex.tokenize(deleteSuspectFromCaseQuery));
		System.out.println(lex.tokenize(categoriesForCaseQuery));
		System.out.println(lex.tokenize(getMaxPersonNoteIdForPersonIdString));
		System.out.println(lex.tokenize(getPersonNotesForPersonString));
		System.out.println(lex.tokenize(updateCaseIsOpenQuery));
		System.out.println(lex.tokenize(convictionsPerCategoryString));
		System.out.println(lex.tokenize(oldestUnresolvedCasesQuery));
		System.out.println(lex.tokenize(literalTest));
	}

}
