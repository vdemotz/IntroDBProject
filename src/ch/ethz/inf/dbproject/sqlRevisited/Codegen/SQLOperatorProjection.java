package ch.ethz.inf.dbproject.sqlRevisited.Codegen;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.SQLPhysicalException;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchemaAttributeDetail;

public class SQLOperatorProjection extends SQLOperatorUnary {

	private final ByteBuffer temporaryBuffer;
	private final TableSchema childSchema;
	public SQLOperatorProjection(TableSchema schema, SQLOperator child) {
		super(schema, child);
		childSchema = getChild().schema;
		temporaryBuffer = ByteBuffer.wrap(new byte[childSchema.getSizeOfEntry()]);
	}

	@Override
	public boolean next(ByteBuffer resultBuffer) throws SQLPhysicalException {
		
		temporaryBuffer.rewind();
		boolean hasNext = getChild().next(temporaryBuffer);
		
		if (hasNext) {
			for (TableSchemaAttributeDetail attribute : schema.getAttributes()) {
				int indexInChildSchema = childSchema.indexOf(attribute.qualifier, attribute.attributeName);
				int byteOffset = childSchema.getSizeOfAttributes(indexInChildSchema);
				resultBuffer.put(Arrays.copyOfRange(temporaryBuffer.array(), childSchema.getSizeOfAttributes(indexInChildSchema),  byteOffset+attribute.attributeType.byteSizeOfType()));
			}
		}
		
		return hasNext;
	}

	@Override
	public SQLOperator copyWithSchema(TableSchema schema) {
		return new SQLOperatorProjection(schema, getChild());
	}

}
