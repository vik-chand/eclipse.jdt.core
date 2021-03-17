/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.dom;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import junit.framework.Test;

@SuppressWarnings("rawtypes")
public class ASTConverter_PreviewTest extends ConverterTestSetup {

	ICompilationUnit workingCopy;

	public void setUpSuite() throws Exception {
		super.setUpSuite();
		this.ast = AST.newAST(getAST16(), false);
		this.currentProject = getJavaProject("Converter_16");
		if (this.ast.apiLevel() == AST.JLS16 ) {
			this.currentProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_16);
			this.currentProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_16);
			this.currentProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_16);

		}
	}

	public ASTConverter_PreviewTest(String name) {
		super(name);
	}

	public static Test suite() {
		return buildModelTestSuite(ASTConverter_PreviewTest.class);
	}

	static int getAST16() {
		return AST.JLS16;
	}
	protected void tearDown() throws Exception {
		super.tearDown();
		if (this.workingCopy != null) {
			this.workingCopy.discardWorkingCopy();
			this.workingCopy = null;
		}
	}

	public void testSealed001() throws CoreException {
		if (!isJRE16) {
			System.err.println("Test "+getName()+" requires a JRE 16");
			return;
		}
		String contents = "public sealed class X permits X1{\n" +
				"\n" +
				"}\n" +
				"non-sealed class X1 extends X {\n" +
				"\n" +
				"}\n";
		this.workingCopy = getWorkingCopy("/Converter_16/src/X.java", true/*resolve*/);
		IJavaProject javaProject = this.workingCopy.getJavaProject();
		String old = javaProject.getOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, true);
		try {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, JavaCore.ENABLED);
			javaProject.setOption(JavaCore.COMPILER_PB_REPORT_PREVIEW_FEATURES, JavaCore.IGNORE);
			ASTNode node = buildAST(
				contents,
				this.workingCopy);
			assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, node.getNodeType());
			CompilationUnit compilationUnit = (CompilationUnit) node;
			assertProblemsSize(compilationUnit, 0);
			node = ((AbstractTypeDeclaration)compilationUnit.types().get(0));
			assertEquals("Not a Type Declaration", ASTNode.TYPE_DECLARATION, node.getNodeType());
			TypeDeclaration type = (TypeDeclaration)node;
			List modifiers = type.modifiers();
			assertEquals("Incorrect no of modifiers", 2, modifiers.size());
			Modifier modifier = (Modifier) modifiers.get(1);
			assertSame("Incorrect modifier keyword", Modifier.ModifierKeyword.SEALED_KEYWORD, modifier.getKeyword());
			List permittedTypes = type.permittedTypes();
			assertEquals("Incorrect no of permits", 1, permittedTypes.size());
			assertEquals("Incorrect type of permit", "org.eclipse.jdt.core.dom.SimpleType", permittedTypes.get(0).getClass().getName());
			node = ((AbstractTypeDeclaration)compilationUnit.types().get(1));
			assertEquals("Not a Type Declaration", ASTNode.TYPE_DECLARATION, node.getNodeType());
			type = (TypeDeclaration)node;
			modifiers = type.modifiers();
			assertEquals("Incorrect no of modfiers", 1, modifiers.size());
			modifier = (Modifier) modifiers.get(0);
			assertSame("Incorrect modifier keyword", Modifier.ModifierKeyword.NON_SEALED_KEYWORD, modifier.getKeyword());

		} finally {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, old);
		}
	}

	public void testSealed002() throws CoreException {
		if (!isJRE16) {
			System.err.println("Test "+getName()+" requires a JRE 16");
			return;
		}
		String contents = "public sealed interface X permits X1{\n" +
				"\n" +
				"}\n" +
				"non-sealed interface X1 extends X {\n" +
				"\n" +
				"}\n";
		this.workingCopy = getWorkingCopy("/Converter_16/src/X.java", true/*resolve*/);
		IJavaProject javaProject = this.workingCopy.getJavaProject();
		String old = javaProject.getOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, true);
		try {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, JavaCore.ENABLED);
			javaProject.setOption(JavaCore.COMPILER_PB_REPORT_PREVIEW_FEATURES, JavaCore.IGNORE);
			ASTNode node = buildAST(
				contents,
				this.workingCopy);
			assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, node.getNodeType());
			CompilationUnit compilationUnit = (CompilationUnit) node;
			assertProblemsSize(compilationUnit, 0);
			node = ((AbstractTypeDeclaration)compilationUnit.types().get(0));
			assertEquals("Not a Record Declaration", ASTNode.TYPE_DECLARATION, node.getNodeType());
			TypeDeclaration type = (TypeDeclaration)node;
			List modifiers = type.modifiers();
			assertEquals("Incorrect no of modfiers", 2, modifiers.size());
			Modifier modifier = (Modifier) modifiers.get(1);
			assertSame("Incorrect modifier keyword", Modifier.ModifierKeyword.SEALED_KEYWORD, modifier.getKeyword());

		} finally {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, old);
		}
	}

	public void testSealed003() throws CoreException {
		if (!isJRE16) {
			System.err.println("Test "+getName()+" requires a JRE 16");
			return;
		}
		String contents = "public sealed interface X permits X1{\n" +
				"\n" +
				"}\n" +
				"non-sealed interface X1 extends X {\n" +
				"\n" +
				"}\n";
		this.workingCopy = getWorkingCopy("/Converter_16/src/X.java", true/*resolve*/);
		IJavaProject javaProject = this.workingCopy.getJavaProject();
		String old = javaProject.getOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, true);
		try {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, JavaCore.ENABLED);
			javaProject.setOption(JavaCore.COMPILER_PB_REPORT_PREVIEW_FEATURES, JavaCore.IGNORE);
			ASTNode node = buildAST(
				contents,
				this.workingCopy);
			assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, node.getNodeType());
			CompilationUnit compilationUnit = (CompilationUnit) node;
			assertProblemsSize(compilationUnit, 0);
			List<AbstractTypeDeclaration> types = compilationUnit.types();
			assertEquals("No. of Types is not 2", types.size(), 2);
			AbstractTypeDeclaration type = types.get(0);
			if (!type.getName().getIdentifier().equals("X")) {
				type = types.get(1);
			}
			assertTrue("type not a type", type instanceof TypeDeclaration);
			TypeDeclaration typeDecl = (TypeDeclaration)type;
			assertTrue("type not an interface", typeDecl.isInterface());
			List modifiers = type.modifiers();
			assertEquals("Incorrect no of modifiers", 2, modifiers.size());
			Modifier modifier = (Modifier) modifiers.get(1);
			assertSame("Incorrect modifier keyword", Modifier.ModifierKeyword.SEALED_KEYWORD, modifier.getKeyword());
			int startPos = modifier.getStartPosition();
			assertEquals("Restricter identifier position for sealed is not 7", startPos, contents.indexOf("sealed"));
			startPos = typeDecl.getRestrictedIdentifierStartPosition();
			assertEquals("Restricter identifier position for permits is not 26", startPos, contents.indexOf("permits"));
		} finally {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, old);
		}
	}

	public void testSealed004() throws CoreException {
		if (!isJRE16) {
			System.err.println("Test "+getName()+" requires a JRE 16");
			return;
		}
		String contents = "public sealed class X permits X1{\n" +
				"\n" +
				"}\n" +
				"non-sealed class X1 extends X {\n" +
				"\n" +
				"}\n";
		this.workingCopy = getWorkingCopy("/Converter_16/src/X.java", true/*resolve*/);
		IJavaProject javaProject = this.workingCopy.getJavaProject();
		String old = javaProject.getOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, true);
		try {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, JavaCore.ENABLED);
			javaProject.setOption(JavaCore.COMPILER_PB_REPORT_PREVIEW_FEATURES, JavaCore.IGNORE);
			ASTNode node = buildAST(
				contents,
				this.workingCopy);
			assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, node.getNodeType());
			CompilationUnit compilationUnit = (CompilationUnit) node;
			assertProblemsSize(compilationUnit, 0);
			List<AbstractTypeDeclaration> types = compilationUnit.types();
			assertEquals("No. of Types is not 2", types.size(), 2);
			AbstractTypeDeclaration type = types.get(0);
			if (!type.getName().getIdentifier().equals("X")) {
				type = types.get(1);
			}
			assertTrue("type not a type", type instanceof TypeDeclaration);
			TypeDeclaration typeDecl = (TypeDeclaration)type;
			assertTrue("type not an class", !typeDecl.isInterface());
			List modifiers = type.modifiers();
			assertEquals("Incorrect no of modifiers", 2, modifiers.size());
			Modifier modifier = (Modifier) modifiers.get(1);
			assertSame("Incorrect modifier keyword", Modifier.ModifierKeyword.SEALED_KEYWORD, modifier.getKeyword());
			int startPos = modifier.getStartPosition();
			assertEquals("Restricter identifier position for sealed is not 7", startPos, contents.indexOf("sealed"));
			startPos = typeDecl.getRestrictedIdentifierStartPosition();
			assertEquals("Restricter identifier position for permits is not 26", startPos, contents.indexOf("permits"));
		} finally {
			javaProject.setOption(JavaCore.COMPILER_PB_ENABLE_PREVIEW_FEATURES, old);
		}
	}
}
