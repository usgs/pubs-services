package gov.usgs.cida.pubs.domain.sipp;

public final class NoteHelper {
	private NoteHelper() {
	}

	public static Note ONE = new Note();
	static {
		ONE.setNoteComment("   note1   ");
	}

	public static Note TWO = new Note();
	static {
		TWO.setNoteComment("   note2   ");
	}

	public static Note THREE = new Note();
	static {
		THREE.setNoteComment("      ");
	}

}
