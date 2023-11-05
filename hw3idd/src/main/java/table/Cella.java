package table;

public class Cella {

	private int colonna;
	private String tableId;
	
	public Cella(int colonna, String tableId) {
		this.colonna = colonna;
		this.tableId = tableId;
	}
	
	public int getColonna() {
		return colonna;
	}
	
	public void setColonna(int colonna) {
		this.colonna = colonna;
	}
	
	public String getTableId() {
		return tableId;
	}
	
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

}
