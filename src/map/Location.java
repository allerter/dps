package map;

public class Location {
    int row;
    int column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setColumn(int column) {
        this.column = column;
    }
}
