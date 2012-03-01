package nextmethod.web.mvc;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 *
 */
public class MetaClassInfoBuilder extends EmptyVisitor {

	// visit
	private int version;
	private int access;
	private String name;
	private String signature;
	private String superName;
	private String[] interfaces;


	@Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
		this.version = version;
		this.access = access;
		this.name = name;
		this.signature = signature;
		this.superName = superName;
		this.interfaces = interfaces;
	}

	// visitOuterClass
	private String visitOuterOwner;
	private String visitOuterName;
	private String visitOuterDesc;

	@Override
	public void visitOuterClass(final String owner, final String name, final String desc) {
		this.visitOuterOwner = owner;
		this.visitOuterName = name;
		this.visitOuterDesc = desc;
	}

	// visitAnnotation
	private String visitAnnDesc;
	private boolean visitAnnVisible;

	@Override
	public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
		this.visitAnnDesc = desc;
		this.visitAnnVisible = visible;
		return null;
	}

	// visitAttribute
	private Attribute attribute;

	@Override
	public void visitAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}

	private String innerName;
	private String innerOuterName;
	private String innerInnerName;
	private int innerAccess;

	@Override
	public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
		this.innerName = name;
		this.innerOuterName = outerName;
		this.innerInnerName = innerName;
		this.innerAccess = access;
	}

	@Override
	public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		return null;
	}
}
