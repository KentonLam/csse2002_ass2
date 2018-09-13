/* This file tests to see if an Assignment submission contains the methods
   specified in the Javadoc specification. It checks for presence of classes and
   presence of methods with correct names, arguments, access and thrown
   exceptions.

   This file does *not* contain functionality tests or checks for spurious
   imports. Using these tests do not guarantee any marks, rather the
   file is just provided in case it is helpful.*/

import csse2002.block.world.*;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.rules.Timeout;

import java.lang.reflect.*;


public class TestMethodsAssignment2 {


    // set a 20 second timeout on all test cases.
    @Rule
    public Timeout gt=Timeout.seconds(20);

    // constants for NO_ARGS and NO_EXCEPTIONS
    // are just empty arrays
    public static String [] NO_ARGS = {};
    public static String [] NO_EXCEPTIONS = {};

    private static class ExpectedMethod {
        /* A helper class to store information
           about a method. The information is checked
           using the reflection API (below).*/

        public String methodName;
        public String returnType;
        public List<String> argumentTypes;
        public List<String> exceptionTypes;
        public int modifiers;

        public ExpectedMethod(String methodName, String returnType,
                              String [] argumentTypes) {
            /* Constructors in this class are just
               a quick way to fill in the fields. */
            this.methodName = methodName;
            this.returnType = returnType;
            this.argumentTypes =
                new ArrayList<>(Arrays.<String>asList(argumentTypes));
            this.exceptionTypes =
                new ArrayList<>(Arrays.<String>asList(NO_EXCEPTIONS));
            modifiers = Modifier.PUBLIC;
        }

        public ExpectedMethod(String methodName, String returnType,
                              String [] argumentTypes,
                              String [] exceptionTypes) {
            /* Constructors in this class are just
               a quick way to fill in the fields. */
            this.methodName = methodName;
            this.returnType = returnType;
            this.argumentTypes =
                new ArrayList<>(Arrays.<String>asList(argumentTypes));
            this.exceptionTypes =
                new ArrayList<>(Arrays.<String>asList(exceptionTypes));
            modifiers = Modifier.PUBLIC;
        }

        public ExpectedMethod(String methodName, String returnType,
                              String [] argumentTypes,
                              String [] exceptionTypes, int modifiers) {
            /* Constructors in this class are just
               a quick way to fill in the fields. */
            this.methodName = methodName;
            this.returnType = returnType;
            this.argumentTypes =
                new ArrayList<>(Arrays.<String>asList(argumentTypes));
            this.exceptionTypes =
                new ArrayList<>(Arrays.<String>asList(exceptionTypes));
            this.modifiers = modifiers;
        }

    }

    @Test
    public void testActionMethods() {
        Class current = Action.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Object");
        assertTrue(current.getInterfaces().length == 0);

        String errorMsg = "In class Action: DIG, DROP, MOVE_BLOCK or MOVE_BUILDER ";

        try {
            Field [] fields = {
                current.getDeclaredField("DIG"),
                current.getDeclaredField("DROP"),
                current.getDeclaredField("MOVE_BLOCK"),
                current.getDeclaredField("MOVE_BUILDER")
            };

            for (Field field: fields) {
                // all 4 fields are public
                assertTrue(errorMsg + "are not public", (field.getModifiers() & Modifier.PUBLIC) > 0);

                // all 4 fields are static
                assertTrue(errorMsg + "are not static", (field.getModifiers() & Modifier.STATIC) > 0);

                // all 4 fields are final
                assertTrue(errorMsg + "are not final", (field.getModifiers() & Modifier.FINAL) > 0);
            }
        } catch (NoSuchFieldException noSuchField) {
            fail(errorMsg + "are not declared");
        }

        // check for constructors
        errorMsg = "In class Action: constructor with args (int, String) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(int.class,
                                                     String.class);
            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);
        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        // check for expected members
        List<ExpectedMethod> expectedFields = new ArrayList<ExpectedMethod>();
        expectedFields.add(new ExpectedMethod("getPrimaryAction", "int",
                                              NO_ARGS, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("getSecondaryAction",
                                              "java.lang.String", NO_ARGS,
                                              NO_EXCEPTIONS));
        String [] loadActionArgs = {"java.io.BufferedReader"};
        String [] loadActionExceptions = {"csse2002.block.world.ActionFormatException"};
        expectedFields.add(new ExpectedMethod("loadAction",
                                            "csse2002.block.world.Action",
                                            loadActionArgs,
                                            loadActionExceptions,
                                            Modifier.PUBLIC | Modifier.STATIC));

        String [] processActionArgs = {"csse2002.block.world.Action","csse2002.block.world.WorldMap"};
        expectedFields.add(new ExpectedMethod("processAction",
                                            "void", processActionArgs,
                                            NO_EXCEPTIONS,
                                            Modifier.PUBLIC | Modifier.STATIC));

        String [] processActionsArgs = {"java.io.BufferedReader", "csse2002.block.world.WorldMap"};
        String [] processActionsExceptions = {"csse2002.block.world.ActionFormatException"};
        expectedFields.add(new ExpectedMethod("processActions",
                                            "void", processActionsArgs,
                                            processActionsExceptions,
                                            Modifier.PUBLIC | Modifier.STATIC));
        checkExpectedMethods(current.getMethods(), expectedFields);

        assertTrue("The number of public members in Action (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Object.class)) +
                   ") is different from the specification (" +
                   expectedFields.size() + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Object.class) == expectedFields.size());
    }

    @Test
    public void testActionFormatExceptionMethods() {
        Class current = ActionFormatException.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Exception");
        assertTrue(current.getInterfaces().length == 0);


        // check for constructors
        String errorMsg = "In class ActionFormatException: constructor with args () ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor();

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        // check for constructors
        errorMsg = "In class ActionFormatException: constructor with args (String) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(String.class);

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        assertTrue("The number of public members in ActionFormatException (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Exception.class)) +
                   ") is different from the specification (" +
                   0 + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Exception.class) == 0);
    }

    @Test
    public void testMainMethods() {
        Class current = Main.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Object");
        assertTrue(current.getInterfaces().length == 0);

        // check for constructors
        String errorMsg = "In class Main: constructors ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor();

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

            assertFalse(errorMsg + "were declared",
                current.getDeclaredConstructors().length > 1);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " were declared.");
        }

        List<ExpectedMethod> expectedFields = new ArrayList<ExpectedMethod>();
        String [] mainArgs = {"[Ljava.lang.String;"};
        expectedFields.add(new ExpectedMethod("main", "void",
                                              mainArgs, NO_EXCEPTIONS,
                                              Modifier.PUBLIC & Modifier.STATIC));
        checkExpectedMethods(current.getMethods(), expectedFields);

        assertTrue("The number of public members in ActionFormatException (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Object.class)) +
                   ") is different from the specification (" +
                   expectedFields.size() + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Object.class) == expectedFields.size());
    }

    @Test
    public void testPositionMethods() {
        Class current = Position.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Object");
        assertOnlyImplements(current, Comparable.class);

        // check for constructors
        String errorMsg = "In class Position: constructor with args (int, int) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(int.class, int.class);

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

            assertTrue(errorMsg + "is not only constructor",
                current.getDeclaredConstructors().length == 1);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        List<ExpectedMethod> expectedFields = new ArrayList<ExpectedMethod>();
        String [] compareToArgs = {"csse2002.block.world.Position"};
        expectedFields.add(new ExpectedMethod("compareTo", "int",
                                              compareToArgs, NO_EXCEPTIONS));

        String [] equalsArgs = {"java.lang.Object"};
        expectedFields.add(new ExpectedMethod("equals", "boolean",
                                            equalsArgs, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("getX", "int",
                                              NO_ARGS, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("getY", "int",
                                             NO_ARGS, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("hashCode", "int",
                                              NO_ARGS, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("toString", "java.lang.String",
                                             NO_ARGS, NO_EXCEPTIONS));
        checkExpectedMethods(current.getMethods(), expectedFields);

        // take into account the two overridden members from Object
        assertTrue("The number of public members in Position (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Object.class) + 2) +
                   ") is different from the specification (" +
                   expectedFields.size() + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Object.class) + 2 == expectedFields.size());

    }

    @Test
    public void testSparseTileArray() {
        Class current = SparseTileArray.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Object");
        assertTrue(current.getInterfaces().length == 0);


        // check for constructors
        String errorMsg = "In class SparseTileArray: constructor with args () ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor();

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

            assertTrue(errorMsg + "is not only constructor",
                current.getDeclaredConstructors().length == 1);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        List<ExpectedMethod> expectedFields = new ArrayList<ExpectedMethod>();
        String [] addLinkedTilesArgs = {"csse2002.block.world.Tile", "int", "int"};
        String [] addLinkedTilesExceptions = {"csse2002.block.world.WorldMapInconsistentException"};
        expectedFields.add(new ExpectedMethod("addLinkedTiles", "void",
                                              addLinkedTilesArgs, addLinkedTilesExceptions));

        String [] getTileArgs = {"csse2002.block.world.Position"};
        expectedFields.add(new ExpectedMethod("getTile", "csse2002.block.world.Tile",
                                            getTileArgs, NO_EXCEPTIONS));
        expectedFields.add(new ExpectedMethod("getTiles", "java.util.List",
                                              NO_ARGS, NO_EXCEPTIONS));

        checkExpectedMethods(current.getMethods(), expectedFields);

        // take into account the two overridden members from Object
        assertTrue("The number of public members in SparseTileArray (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Object.class)) +
                   ") is different from the specification (" +
                   expectedFields.size() + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Object.class) == expectedFields.size());

    }

    @Test
    public void testWorldMap() {
        Class current = WorldMap.class;

        assertNotAbstract(current);
        assertSuperclass(current, "java.lang.Object");
        assertTrue(current.getInterfaces().length == 0);


        // check for constructors
        String errorMsg = "In class WorldMap: constructor with args (String) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(String.class);

            List<Class> exceptionTypes = Arrays.<Class>asList(constructor.getExceptionTypes());
            assertTrue(errorMsg + "throws wrong number of exceptions (" +
                       exceptionTypes.size() + ") should be (3)",
                       exceptionTypes.size() == 3);

            Class [] expectedExceptionsArray = {WorldMapFormatException.class,
                 WorldMapInconsistentException.class, java.io.FileNotFoundException.class};
            List<Class> expectedExceptions = Arrays.<Class>asList(expectedExceptionsArray);

            for (int i = 0; i < expectedExceptions.size(); i++) {
                assertTrue(errorMsg + "missing expected exception",
                    exceptionTypes.contains(expectedExceptions.get(i)));
            }

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        errorMsg = "In class WorldMap: constructor with args (Tile, Position, Builder) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(csse2002.block.world.Tile.class,
                    csse2002.block.world.Position.class,
                    csse2002.block.world.Builder.class);

            List<Class> exceptionTypes = Arrays.<Class>asList(constructor.getExceptionTypes());
            assertTrue(errorMsg + "throws wrong number of exceptions (" +
                       exceptionTypes.size() + ") should be (1)",
                       exceptionTypes.size() == 1);

            Class [] expectedExceptionsArray = {WorldMapInconsistentException.class};
            List<Class> expectedExceptions = Arrays.<Class>asList(expectedExceptionsArray);

            for (int i = 0; i < expectedExceptions.size(); i++) {
                assertTrue(errorMsg + "missing expected exception",
                    exceptionTypes.contains(expectedExceptions.get(i)));
            }

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        assertTrue("In class WorldMap: more than two constructors declared.",
            current.getDeclaredConstructors().length == 2);

        List<ExpectedMethod> expectedFields = new ArrayList<ExpectedMethod>();
        String [] addLinkedTilesExceptions = {"csse2002.block.world.WorldMapInconsistentException"};
        expectedFields.add(new ExpectedMethod("getBuilder", "csse2002.block.world.Builder",
                NO_ARGS, NO_EXCEPTIONS));

        expectedFields.add(new ExpectedMethod("getStartPosition", "csse2002.block.world.Position",
                NO_ARGS, NO_EXCEPTIONS));

        String [] getTileArgs = {"csse2002.block.world.Position"};
        expectedFields.add(new ExpectedMethod("getTile", "csse2002.block.world.Tile",
                                              getTileArgs, NO_EXCEPTIONS));

        expectedFields.add(new ExpectedMethod("getTiles", "java.util.List",
                                            NO_ARGS, NO_EXCEPTIONS));

        String [] saveMapArgs = {"java.lang.String"};
        String [] saveMapExceptions = {"java.io.IOException"};
        expectedFields.add(new ExpectedMethod("saveMap", "void",
                                              saveMapArgs, saveMapExceptions));

        checkExpectedMethods(current.getMethods(), expectedFields);

        // take into account the two overridden members from Object
        assertTrue("The number of public members in WorldMap (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Object.class)) +
                   ") is different from the specification (" +
                   expectedFields.size() + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Object.class) == expectedFields.size());

    }

    @Test
    public void testWorldMapFormatException() {
        Class current = WorldMapFormatException.class;

        assertNotAbstract(current);
        assertSuperclass(current, "csse2002.block.world.BlockWorldException");
        assertTrue(current.getInterfaces().length == 0);


        // check for constructors
        String errorMsg = "In class WorldMapFormatException: constructor with args () ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor();

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        // check for constructors
        errorMsg = "In class WorldMapFormatException: constructor with args (String) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(String.class);

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        assertTrue("The number of public members in WorldMapFormatException (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Exception.class)) +
                   ") is different from the specification (" +
                   0 + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Exception.class) == 0);
    }

    @Test
    public void testWorldMapInconsistentException() {
        Class current = WorldMapInconsistentException.class;

        assertNotAbstract(current);
        assertSuperclass(current, "csse2002.block.world.BlockWorldException");
        assertTrue(current.getInterfaces().length == 0);


        // check for constructors
        String errorMsg = "In class WorldMapInconsistentException: constructor with args () ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor();

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        // check for constructors
        errorMsg = "In class WorldMapInconsistentException: constructor with args (String) ";
        try {
            Constructor constructor =
                current.getDeclaredConstructor(String.class);

            assertTrue(errorMsg + "throws exceptions (" +
                       constructor.getExceptionTypes().length + ")",
                       constructor.getExceptionTypes().length == 0);

        } catch(NoSuchMethodException noSuchMethod) {
            fail(errorMsg + " is not declared.");
        }

        assertTrue("The number of public members in WorldMapInconsistentException (" +
                   (countPublicMembersIn(current) - countPublicMembersIn(Exception.class)) +
                   ") is different from the specification (" +
                   0 + ")",
                   countPublicMembersIn(current) - countPublicMembersIn(Exception.class) == 0);
    }


    /*
     * Assert whether a class is an interface.
     */
    private void assertInterface(Class klass) {
        assertTrue((klass.getModifiers() & Modifier.INTERFACE) > 0);
    }

    /*
     * Assert that a class is not an interface, and not abstract.
     */
    private void assertNotAbstract(Class klass) {
        assertFalse((klass.getModifiers() &
                     Modifier.INTERFACE) > 0);
        assertFalse((klass.getModifiers() &
                     Modifier.ABSTRACT) > 0);
    }

    /*
     * Assert that a class has a certain superclass.
     */
    private void assertSuperclass(Class klass, String superclass) {
        assertTrue(klass.getSuperclass()
                   .getName().equals(superclass));
    }

    /*
     * Assert that one class implements an interface.
     */
    private void assertImplements(Class klass, Class interfase) {
        List<Class> interfaces =
            Arrays.asList(klass.getInterfaces());
        assertTrue(interfaces.contains(interfase));
    }

    /*
     * Assert that one class only implements an interface.
     */     
    private void assertOnlyImplements(Class klass, Class interfase) {
        List<Class> interfaces =
            Arrays.asList(klass.getInterfaces());
        assertTrue(interfaces.contains(interfase) && interfaces.size() == 1);
    }

    /*
     * Count the number of public member functions in a class.
     */
    private int countPublicMembersIn(Class klass) {
        int publicMembers = 0;

        for (Method method : klass.getMethods()) {
            if ((method.getModifiers() & Modifier.PUBLIC) > 0) {
                publicMembers++;
            }
        }

        return publicMembers;
    }

    /*
     * Count the number of public variables in a class.
     */
    private int countPublicVariablesIn(Class klass) {
        int publicVariables = 0;

        for (Field field : klass.getFields()) {
            if ((field.getModifiers() & Modifier.PUBLIC) > 0) {
                publicVariables++;
            }
        }

        return publicVariables;
    }

    /*
     * Check that expected fields all exist within methods using assertions.
     */
    private void checkExpectedMethods(Method [] methods,
                                      List<ExpectedMethod> expectedFields) {

        List<String> methodNames = new ArrayList<>();
        for (Method method : methods) {
            methodNames.add(method.getName());
        }

        for (ExpectedMethod expectedMethod : expectedFields) {

            // check if field exists
            Method method = null;

            for (int i = 0; i < methods.length; i++) {
                // if name and arguments match
                if (methods[i].getName() == expectedMethod.methodName &&
                    checkArgumentTypes(methods[i],
                                       expectedMethod.argumentTypes)) {
                    method = methods[i];
                }

                //if (methods[i].getName() == expectedMethod.methodName) {
                //    System.out.println(methods[i]);
                //    System.out.println(expectedMethod.argumentTypes);
                //}
            }

            // if no matching method is found, method will be null
            assertTrue(
                "No method matching " + expectedMethod.methodName +
                " with args " + expectedMethod.argumentTypes,
                method != null);

            // check that the type is the expected type
            assertTrue(
                "Mismatched return type (" + expectedMethod.returnType +
                ") for  " + expectedMethod.methodName + " with args " +
                expectedMethod.argumentTypes + " that returns (" +
                method.getReturnType().getName() + ")",
                method.getReturnType().getName().equals(expectedMethod.
                                                        returnType));

            // check that the field is public
            assertTrue(
                "Method " + expectedMethod.methodName + " with args " +
                expectedMethod.argumentTypes + " is not public",
                (method.getModifiers() & Modifier.PUBLIC) > 0);

            // check that other modifiers match
            assertTrue(
                "Method " + expectedMethod.methodName + " with args " +
                expectedMethod.argumentTypes + " has differing modifiers " +
                " (e.g. final, static, etc)",
                (method.getModifiers() & expectedMethod.modifiers) == expectedMethod.modifiers);

            // check that the correct exceptions are thrown
            assertTrue("Method " + expectedMethod.methodName +
                       " with args " + expectedMethod.argumentTypes +
                       " does not throw the correct exceptions",
                       checkThrownExceptions(
                           method,
                           expectedMethod.exceptionTypes));
        }
    }

    /*
     * Check that the parameters match those expected.
     */
    private boolean checkArgumentTypes(Method method,
                                       List<String> argumentTypes) {

        if (method.getParameterTypes().length != argumentTypes.size()) {
            return false;
        }

        for (int i = 0; i < argumentTypes.size(); i++) {
            //System.out.println(method.getParameterTypes()[i].getName());
            if (!method.getParameterTypes()[i].getName().equals(
                    argumentTypes.get(i))) {
                return false;
            }
        }

        return true;
    }

    /*
     * Check that the thrown exceptions match those expected.
     */
    private boolean checkThrownExceptions(Method method,
                                          List<String> exceptionTypes) {
        if (method.getExceptionTypes().length != exceptionTypes.size()) {
            System.out.println("Sizes mismatch");
            return false;
        }

        List<Class> methodExceptionTypes =
            new ArrayList<>(Arrays.asList(method.getExceptionTypes()));

        // sort both
        methodExceptionTypes.sort(new java.util.Comparator<Class>() {
            @Override public int compare(Class klass1, Class klass2) {
                return klass1.getName().compareTo(klass2.getName());
            }
        });
        exceptionTypes.sort(null);

        for (int i = 0; i < exceptionTypes.size(); i++) {
            if (!methodExceptionTypes.get(i).getName().equals(exceptionTypes
                                                              .get(i))) {
                //System.out.println(methodExceptionTypes.get(i));
                //System.out.println(exceptionTypes.get(i));
                return false;
            }
        }

        return true;
    }

}
