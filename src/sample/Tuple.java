package sample;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Tuple {
    private List<Tuple> path;
    private List<Tuple> children;
    private int weight;
    public int row, col;
    public Tuple parent;

    public Tuple(int i, int j, Tuple parent) {
        this(i, j, new LinkedList<>(), 0, new LinkedList<>(), parent);

    }

    // compute opposite node given that it is in the other direction from the parent
    public Tuple opposite() {
        if (row - parent.row != 0)
            return new Tuple(row + row - parent.row, col, this);
        if (col - parent.col != 0)
            return new Tuple(row, col + col - parent.col, this);
        return null;
    }
    public Tuple(int i, int j) {
        this(i, j, new LinkedList<>(), 0, new LinkedList<>(), null);
    }

    public Tuple(int i, int j, int weight, List<Tuple> path) {
        this(i, j, new LinkedList<>(), weight, path, null);
    }

    public Tuple(int i, int j, List<Tuple> children, int weight, List<Tuple> path, Tuple parent) {
        this.row = i;
        this.col = j;
        this.path = path;
        this.children = children;
        this.weight = weight;
        this.parent = parent;
    }

    public List<Tuple> getPath() {
        return path;
    }

    public void setPath(List<Tuple> path) {
        this.path = path;
    }

    public List<Tuple> getChildren() {
        return children;
    }

    public void setChildren(List<Tuple> children) {
        this.children = children;
    }

    public void addChild(Tuple child) {
        children.add(child);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tuple) {
            Tuple toCompare = (Tuple) o;
            return toCompare.col == this.col && toCompare.row == this.row;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public String toString() {
        return String.format("(%d, %d)", row, col);
    }

}