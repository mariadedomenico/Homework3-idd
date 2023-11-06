package table;

public class Cella {

	private String colonna;
	private String tableId;
	
	public Cella(String colonna, String tableId) {
		this.colonna = colonna;
		this.tableId = tableId;
	}
	
	public String getColonna() {
		return colonna;
	}
	
	public void setColonna(String colonna) {
		this.colonna = colonna;
	}
	
	public String getTableId() {
		return tableId;
	}
	
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	@Override
	public int hashCode() {
		return this.colonna.hashCode() + this.tableId.hashCode() + 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Cella other = (Cella) obj;
		return this.colonna.equals(other.getColonna()) && this.tableId.equals(other.getTableId());
	}
	
	

}
