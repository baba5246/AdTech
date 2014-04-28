JAVA_BASE=/System/Library/Frameworks/JavaVM.framework/Versions/A
JAVA_HOME=$(JAVA_BASE)/Home

TARGET=MeCab
JAVAC=javac -J-Dfile.encoding=UTF-8
JAVA=java -Dfile.encoding=UTF-8
JAR=jar -J-Dfile.encoding=UTF-8
CXX=g++
INCLUDE=$(JAVA_BASE)/Headers

PACKAGE=org/chasen/mecab

LIBS=-arch x86_64 `mecab-config --libs`
INC=-arch x86_64 `mecab-config --cflags` -I$(INCLUDE)

all:
	$(CXX) -O3 -c -fpic $(TARGET)_wrap.cxx  $(INC)
	$(CXX) -dynamiclib  $(TARGET)_wrap.o -o lib$(TARGET).so $(LIBS)
	$(JAVAC) $(PACKAGE)/*.java
	$(JAVAC) test.java
	$(JAR) cfv $(TARGET).jar $(PACKAGE)/*.class

test:
	env DYLD_LIBRARY_PATH=. $(JAVA) test

clean:
	rm -fr *.jar *.o *.dylib *.so *.class $(PACKAGE)/*.class
	
cleanall:
	rm -fr $(TARGET).java *.cxxmecab-java
