# ExpressionParser
A program that would parse and evaluate a complex mathematical string representation of an equation of n'th degree.

Example Output :


Input 1: (x+y)^2

    [^, [+, [x], [y]], [2]]
    
    x^2 + 2xy + y^2

Input 2: x^2-y^3

    [+, [^, [x], [2]], -[^, [y], [3]]]
    
    - y^3 + x^2

Input 3: (x+y)*(x-y)

    [*, [+, [x], [y]], [+, [x], [-y]]]
    
    x^2 - y^2

Input 4: (x+y)^2-(x-y)^2

    [+, [^, [+, [x], [y]], [2]], -[^, [+, [x], [-y]], [2]]]
    
    4xy
