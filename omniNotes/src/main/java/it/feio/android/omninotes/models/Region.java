package it.feio.android.omninotes.models;

public class Region {
    int start;
    int end;

    public Region(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Region{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
