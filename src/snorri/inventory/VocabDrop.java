package snorri.inventory;

import java.awt.Image;

import snorri.grammar.DefaultLexicon;
import snorri.hieroglyphs.Hieroglyphs;

public class VocabDrop implements Droppable, Comparable<VocabDrop> {

	private static final long serialVersionUID = 1L;
	private String orthography;
	
	public VocabDrop(String orthography) {
		this.orthography = orthography;
	}
	
	public String getOrthography() {
		return orthography;
	}
	
	@Override
	public boolean stack(Droppable other) {
		return false;
	}
	
	@Override
	public int getMaxQuantity() {
		return 1;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof VocabDrop)) {
			return false;
		}
		return orthography.equals(((VocabDrop) other).orthography);
	}
	
	@Override
	public String toString() {
		return orthography;
	}

	@Override
	public int compareTo(VocabDrop other) {
		return toUniqueString().compareTo(((VocabDrop) other).toUniqueString());
	}

	@Override
	public Image getTexture() {
		return Hieroglyphs.getImage(orthography);
	}

	@Override
	public Droppable copy() {
		return new VocabDrop(orthography);
	}
	
	public static VocabDrop fromString(String raw) {
		if (DefaultLexicon.lookup(raw) != null) {
			return new VocabDrop(raw);
		}
		return null;
	}
	
}