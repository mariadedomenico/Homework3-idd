package table;

public class Colonna {

	private String colonna;
	private String tableId;
	private String contenuto;
	
	public Colonna(String colonna, String tableId, String contenuto) {
		this.colonna = colonna;
		this.tableId = tableId;
		this.contenuto = contenuto;
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

	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	@Override
	public int hashCode() {
		return this.colonna.hashCode() + this.tableId.hashCode() + 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Colonna other = (Colonna) obj;
		return this.colonna.equals(other.getColonna()) && this.tableId.equals(other.getTableId());
	}
	
	

}
