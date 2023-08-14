# Define compiler variables and jflags
JAVAC = /usr/bin/javac
JFLAGS = -d .

# Src directory and Bin directory
SRCDIR = MonteCarloMini/MonteCarloMinimization.java \ MonteCarloMini/MonteCarloMinimizationParallel.java \ MonteCarloMini/Search.java \ MonteCarloMini/TerrainArea.java \
CLASS_FILES = MonteCarloMini/MonteCarloMinimization.class \ MonteCarloMini/MonteCarloMinimizationParallel.class \ MonteCarloMini/Search.class \ MonteCarloMini/TerrainArea.class \

# Compile Java code
$(CLASS_FILES): $(SRCDIR)
	$(JAVAC) $(JFLAGS) $(SRCDIR)

# Default target
default: $(CLASS_FILES)

# Clean the compiled files
clean:
	rm -f $(CLASS_FILES)

# Run the Serial program
runSerialMonteCarlo: $(CLASS_FILES)
	java MonteCarloMini.MonteCarloMinimization

# Run the Parallel program
runParallelMonteCarlo: $(CLASS_FILES)
	java MonteCarloMini.MonteCarloMinimizationParallel 
