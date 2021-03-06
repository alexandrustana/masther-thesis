package ro.lrg.ctu.tests;

import static org.junit.Assert.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ro.lrg.ctu.tests.util.TestUtil;
import ro.lrg.xcore.metametamodel.Group;
import thesis.metamodel.entity.MClass;
import thesis.metamodel.entity.MClassPair;
import thesis.metamodel.entity.MParameterPair;
import thesis.metamodel.entity.MProject;

class Test1 {
	
	private Group<MClass> allGenericTypes;

	private MClass findClass(String name) {
		for (MClass aClass : allGenericTypes.getElements()) {
			if (aClass.getUnderlyingObject().getFullyQualifiedName().equals(name)) {
				return aClass;
			}
		}
		return null;
	}
	
	@BeforeEach
	public void loadProject() {
		TestUtil.importProject("TestProject1", "TestProject1.zip");
		MProject prj = thesis.metamodel.factory.Factory.getInstance().createMProject(TestUtil.getProject("TestProject1").get());
		allGenericTypes = prj.genericTypesWithBoundedParameterPairs();
	}
	
	@AfterEach
	public void deleteProject() {
		TestUtil.deleteProject("TestProject1");
	}
	
	@Test
	void test1() {
		MClass theClass = findClass("tc1.ClassOne");
		assertNotNull("tc1.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc1.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2) pairs are the POSSIBLE concrete type pairs for tc1.ClassOne.T and tc1.ClassOne.K", 4, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B2) pairs are the USED concrete type pair for tc1.ClassOne.T and tc1.ClassOne.K ", 2, usedClassPairs.getElements().size());
		assertEquals(0.5, paramPair.apertureCoverage(), 0);
	}

	@Test
	void test2() {
		MClass theClass = findClass("tc2.ClassOne");
		assertNotNull("tc2.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc2.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2) pairs are the POSSIBLE concrete type pair for tc2.ClassOne.T and tc2.ClassOne.K", 4, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B2),(A1,B2) pairs are the USED concrete type pair for for tc2.ClassOne.T and tc2.ClassOne.K ", 3, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.75, 0);
	}

	@Test
	void test3() {
		MClass theClass = findClass("tc3.ClassOne");
		assertNotNull("tc3.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc3.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2) pairs are the POSSIBLE concrete type pairs for tc3.ClassOne.T and tc3.ClassOne.K", 4, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B2) pairs are the USED concrete type pair for tc3.ClassOne.T and tc3.ClassOne.K ", 2, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.5, 0);
	}

	@Test
	void test4() {
		MClass theClass = findClass("tc4.ClassOne");
		assertNotNull("tc4.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc4.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2) pairs are the POSSIBLE concrete type pairs for tc4.ClassOne.T and tc4.ClassOne.K", 4, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B2) pairs are the USED concrete type pair for tc4.ClassOne.T and tc4.ClassOne.K ", 2, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.5, 0);
	}

	@Test
	void test5() {
		MClass theClass = findClass("tc5.ClassOne");
		assertNotNull("tc5.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc5.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2) pairs are the POSSIBLE concrete type pairs for tc5.ClassOne.T and tc5.ClassOne.K", 4, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B1) pairs are the USED concrete type pair for tc5.ClassOne.T and tc5.ClassOne.K ", 2, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.5, 0);
	}

	@Test
	void test6() {
		MClass theClass = findClass("tc6.ClassOne");
		assertNotNull("tc6.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc6.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2),(A3,B1),(A3,B2) pairs are the POSSIBLE concrete type pairs for tc6.ClassOne.T and tc6.ClassOne.K", 6, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B1) pairs are the USED concrete type pair for tc6.ClassOne.T and tc6.ClassOne.K ", 2, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.333, 0.1);
	}

	@Test
	void test7() {
		MClass theClass = findClass("tc7.ClassOne");
		assertNotNull("tc7.ClassOne with 2 bounded parametes must be found", theClass);
		
		Group<MParameterPair> typeParameterPairs = theClass.typeParameterPairs();
		assertEquals("There is one pair of bounded type parameters for tc7.ClassOne", 1, typeParameterPairs.getElements().size());
		
		MParameterPair paramPair = typeParameterPairs.getElements().get(0);
		Group<MClassPair> possibleClassPairs = paramPair.possibleConcreteTypes();
		assertEquals("(A1,B1),(A1,B2),(A2,B1),(A2,B2),(A3,B1),(A3,B2) pairs are the POSSIBLE concrete type pairs for tc7.ClassOne.T and tc7.ClassOne.K", 6, possibleClassPairs.getElements().size());
		
		Group<MClassPair> usedClassPairs = paramPair.usedArgumentsTypes();
		assertEquals("(A1,B1),(A2,B1),(A3,B1) pairs are the USED concrete type pair for tc7.ClassOne.T and tc7.ClassOne.K ", 3, usedClassPairs.getElements().size());
		assertEquals(paramPair.apertureCoverage(), 0.5, 0);
	}

}
