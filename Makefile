# Define compiler variables and jflags
JAVAC = javac
JFLAGS = -d .

# Define java source files (.java) and compiled class files (.class)
SRC_FILES = MonteCarloMini/MonteCarloMinimization.java \
            MonteCarloMini/MonteCarloMinimizationParallel.java \
            MonteCarloMini/Search.java \
            MonteCarloMini/TerrainArea.java \

CLASS_FILES = MonteCarloMini/MonteCarloMinimization.class \
              MonteCarloMini/MonteCarloMinimizationParallel.class \
              MonteCarloMini/SearchMultithreader.class \
              MonteCarloMini/Search.class \
              MonteCarloMini/'Search$$Direction.class' \
              MonteCarloMini/'Search$$1.class' \
              MonteCarloMini/TerrainArea.class \

# Instruction to compile code from .java files to .class files
$(CLASS_FILES): $(SRC_FILES)
        $(JAVAC) $(JFLAGS) $(SRC_FILES)

# Default target
default: $(CLASS_FILES)

# Deletes all compiles class files
clean:
        rm -f $(CLASS_FILES)

# Run the Serial program
runSerialMonteCarlo: $(CLASS_FILES)
        java MonteCarloMini.MonteCarloMinimization

# Run the Parallel program
runParallelMonteCarlo: $(CLASS_FILES)
        java MonteCarloMini.MonteCarloMinimizationParallel
