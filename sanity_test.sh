javac EvaluatePolynomials.java
java EvaluatePolynomials < data/sample_input$1.txt > data/sample_progOutput$1.txt
cmp data/sample_progOutput$1.txt data/sample_output$1.txt

if cmp -s data/sample_progOutput$1.txt data/sample_output$1.txt
then
   echo "Sanity Test"$1" Passed!"
else
   echo "Output Differs"
   echo "run vimdiff data/sample_progOutput.txt data/sample_output.txt"
fi

