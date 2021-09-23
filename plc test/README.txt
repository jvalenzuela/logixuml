This directory contains resources for running the PLC-based portion of the
unit tests, where state machine AOIs are imported into an RSLogix project
and validated in runtime, typically with Logix Emulate. The procedure for
performing these tests is as follows:

Running Unit Tests
================================================================================

1. Build the LogixUML module to be tested.

2. Start Modelio and add the newly-built LogixUML module to the Modules
   Catalog, replacing any existing version.

3. Set the Modelio workspace to this directory.

4. Create a new Modelio project. The default name of ProjectX is fine.

5. Deploy the LogixUML module in the new Modelio project.

6. Run the modelio_setup.py Jython script in Modelio.

7. Export the all the state machines in the Modelio project to AOIs.

8. Run the importaoi.py script which will import all the state machine
   AOIs, creating an out.L5X PLC project.

9. Import the out.L5X project into RSLogix.

10. Download the out PLC project to a something that will run the logic, such
    as Logix Emulate; the controller type or slot can be altered as necessary.

11. Test results are available at the top of the results/summary program in
    the results task. Some processor faults will occur as the normal test
    process; further information is provided at the top of the same logic
    routine.


Creating Unit Tests
================================================================================

PLC unit tests require add-on instructions exported from Modelio state
machines by the LogixUML module being tested. These state machines are stored
in this repository as Modelio patterns(umlt extension). The process for
creating additional unit tests and their associated state machines is outlined
below:

1. Create a new Modelio project. This is only a temporary project, so the
   workspace location is irrelevant. Do not deploy the LogixUML module into
   this project.

2. Create a state machine diagram and give it an appropriate name. The
   name must end in an underscore if the new state machine is to be tested
   in every available scan mode. The Modelio setup Jython script will
   automatically generate variants for each scan mode from patterns ending
   with an underscore.

3. Right click on the new state machine and select Patterns -> Create Pattern
   -> Create Pattern from model.

4. Name the pattern and the state machine it contains based on one of the
   following options:

   (a) For state machines that will be tested in multiple scan modes the
       modelio_setup.py Jython script will automatically create multiple
       scan mode variants of this state machine. This function is enabled by
       giving the pattern a name that ends with an underscore, and the name
       of the state machine within the new pattern must be parameterized
       by changing its name to $(name).

   (b) For state machines not requiring testing in multiple scan modes the
       pattern and enclosed state machine must be given the same name, and
       it must not end in an underscore.

5. Export the pattern by right clicking on the pattern and select Patterns ->
   Export Pattern.

6. Copy the pattern file from data/.config/patterns in the Modelio project
   directory into this directory.

7. Rename the pattern file to remove the underscore and version. Remove only
   one underscore; the name should still end with an underscore if the
   pattern was named per step 4.

8. Use the procedure described in Running Unit Tests to create a new Modelio
   project with the new state machine(s).

9. Export the new state machines(s) to AOI L5X files. The actual LogixUML
   module being tested doesn't have to be used for this because these exports
   will only be used for their input and output parameters; the internal logic
   will not used.

10. Open the unittest PLC project in RSLogix.

11. Import the new state machine AOI(s) into the unittest PLC project.

12. Delete all logic from the Logic, Prescan, and EnableInFalse
    routines of the imported AOI(s). This allows the unit test logic to be
    written using the AOI I/O parameters, but ensures tests will fail
    until the actual AOI is imported when running the unit tests.

13. Write the unit test logic.

14. Commit the new state machine pattern(s) and PLC project.
