package ch.ethz.inf.dbproject.sqlRevisited.test;

import java.nio.ByteBuffer;
import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.PhysicalTableIterator;
import ch.ethz.inf.dbproject.sqlRevisited.TableConnection;

public class PhysicalTableIteratorTest {

	@Test
	public void fastTestPhysicalTableIterator() throws Exception{
		Database db = new Database();
		TableConnection tc = db.getTableConnection("Person");
		PhysicalTableIterator pti = (PhysicalTableIterator)tc.getIteratorFirst();
		ByteBuffer buf = ByteBuffer.allocate(tc.getTableSchema().getSizeOfEntry());
		while(pti.next(buf)){
			buf.rewind();
			int key = buf.getInt();
			System.out.println("Entry with key : "+key);
		}
	}
}
