package org.eclipse.jdt.internal.core;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.core.resources.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.jdom.*;

/**
 * Implements functionality common to
 * operations that create type members.
 */
public abstract class CreateTypeMemberOperation extends CreateElementInCUOperation {
	/**
	 * The source code for the new member.
	 */
	protected String fSource = null;
	/**
	 * The name of the <code>DOMNode</code> that may be used to
	 * create this new element.
	 * Used by the <code>CopyElementsOperation</code> for renaming
	 */
	protected String fAlteredName;
	/**
	 * The JDOM document fragment representing the element that
	 * this operation created. 
	 */
	 protected IDOMNode fDOMNode;
/**
 * When executed, this operation will create a type member
 * in the given parent element with the specified source.
 */
public CreateTypeMemberOperation(IJavaElement parentElement, String source, boolean force) {
	super(parentElement);
	fSource= source;
	fForce= force;
}
/**
 * @see CreateElementInCUOperation#generateNewCompilationUnitDOM
 */
protected void generateNewCompilationUnitDOM(ICompilationUnit cu) throws JavaModelException {
	char[] prevSource = cu.getBuffer().getCharacters();

	// create a JDOM for the compilation unit
	fCUDOM = (new DOMFactory()).createCompilationUnit(prevSource, cu.getElementName());
	IDOMNode parent = ((JavaElement) getParentElement()).findNode(fCUDOM);
	if (parent == null) {
		//#findNode does not work for autogenerated CUs as the contents are empty
		parent = fCUDOM;
	}
	IDOMNode child = generateElementDOM();
	if (child != null) {
		insertDOMNode(parent, child);
	}
	worked(1);
}
/**
 * Generates a <code>IDOMNode</code> based on the source of this operation
 * when there is likely a syntax error in the source.
 */
protected IDOMNode generateSyntaxIncorrectDOM() throws JavaModelException {
	//create some dummy source to generate a dom node
	StringBuffer buff = new StringBuffer();
	buff.append(JavaModelManager.LINE_SEPARATOR + " public class A {" + JavaModelManager.LINE_SEPARATOR);
	buff.append(fSource);
	buff.append(JavaModelManager.LINE_SEPARATOR).append('}');
	IDOMCompilationUnit domCU = (new DOMFactory()).createCompilationUnit(buff.toString(), "A.java");
	IDOMNode node = (IDOMNode) domCU.getChild("A").getChildren().nextElement();
	if (node != null) {
		node.remove();
	}
	return node;
}
/**
 * Returns the IType the member is to be created in.
 */
protected IType getType() {
	return (IType)getParentElement();
}
/**
 * Sets the name of the <code>DOMNode</code> that will be used to
 * create this new element.
 * Used by the <code>CopyElementsOperation</code> for renaming
 */
protected void setAlteredName(String newName) {
	fAlteredName = newName;
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - the parent element supplied to the operation is
 * 		<code>null</code>.
 *	<li>INVALID_CONTENTS - The source is <code>null</code> or has serious syntax errors.
  *	<li>NAME_COLLISION - A name collision occurred in the destination
 * </ul>
 */
public IJavaModelStatus verify() {
	IJavaModelStatus status = super.verify();
	if (!status.isOK()) {
		return status;
	}
	if (fSource == null) {
		return new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS);
	}
	if (!fForce) {
		//check for name collisions
		try {
			IDOMNode node= generateElementDOM();
			if (node == null) {
				return new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS);
			}
		} catch (JavaModelException jme) {
		}
		return verifyNameCollision();
	}
	
	return JavaModelStatus.VERIFIED_OK;
}
/**
 * Verify for a name collision in the destination container.
 */
protected IJavaModelStatus verifyNameCollision() {
	return JavaModelStatus.VERIFIED_OK;
}
}
