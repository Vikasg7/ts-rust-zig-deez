(ns monkey-lang.eval-test 
  (:require [clojure.test :refer [deftest is testing]]
            [monkey-lang.eval :as eval]
            [monkey-lang.parser :as parser]
            [monkey-lang.object :as object]))

(def evaluate (comp object/value eval/run parser/run))

(deftest name-test
  (testing "Integar Literals"
    (is (= (evaluate "5;") 5)))
  (testing "Boolean Literals"
    (is (= (evaluate "true;") true))
    (is (= (evaluate "false;") false)))
  (testing "Prefix Bang Operator"
    (is (= (evaluate "!true") false))
    (is (= (evaluate "!false") true))
    (is (= (evaluate "!5") false))
    (is (= (evaluate "!!true") true))
    (is (= (evaluate "!!false") false))
    (is (= (evaluate "!!5") true)))
  (testing "Interger Expressions"
    (is (= (evaluate "5") 5))
    (is (= (evaluate "10") 10))
    (is (= (evaluate "-5") -5))
    (is (= (evaluate "-10") -10))
    (is (= (evaluate "5 + 5 + 5 + 5 - 10") 10))
    (is (= (evaluate "2 * 2 * 2 * 2 * 2") 32))
    (is (= (evaluate "-50 + 100 + -50") 0))
    (is (= (evaluate "5 * 2 + 10") 20))
    (is (= (evaluate "5 + 2 * 10") 25))
    (is (= (evaluate "20 + 2 * -10") 0))
    (is (= (evaluate "50 / 2 * 2 + 10") 60))
    (is (= (evaluate "2 * (5 + 10)") 30))
    (is (= (evaluate "3 * 3 * 3 + 10") 37))
    (is (= (evaluate "3 * (3 * 3) + 10") 37))
    (is (= (evaluate "(5 + 10 * 2 + 15 / 3) * 2 + -10") 50)))
  (testing "Infix Boolean Expressions"
    (is (= (evaluate "true") true))
    (is (= (evaluate "false") false))
    (is (= (evaluate "1 < 2") true))
    (is (= (evaluate "1 > 2") false))
    (is (= (evaluate "1 < 1") false))
    (is (= (evaluate "1 > 1") false))
    (is (= (evaluate "1 == 1") true))
    (is (= (evaluate "1 != 1") false))
    (is (= (evaluate "1 == 2") false))
    (is (= (evaluate "1 != 2") true))
    (is (= (evaluate "true == true") true))
    (is (= (evaluate "false == false") true))
    (is (= (evaluate "true == false") false))
    (is (= (evaluate "true != false") true))
    (is (= (evaluate "false != true") true))
    (is (= (evaluate "(1 < 2) == true") true))
    (is (= (evaluate "(1 < 2) == false") false))
    (is (= (evaluate "(1 > 2) == true") false))
    (is (= (evaluate "(1 > 2) == false") true)))
  (testing "If Else Expressions"
    (is (= (evaluate "if (true) { 10 }") 10))
    (is (= (evaluate "if (false) { 10 }") nil))
    (is (= (evaluate "if (1) { 10 }") 10))
    (is (= (evaluate "if (1 < 2) { 10 }") 10))
    (is (= (evaluate "if (1 > 2) { 10 }") nil))
    (is (= (evaluate "if (1 > 2) { 10 } else { 20 }") 20))
    (is (= (evaluate "if (1 < 2) { 10 } else { 20 }") 10)))
  (testing "Return Statements"
    (is (= (evaluate "return 10;") 10))
    (is (= (evaluate "return 10; 9;") 10))
    (is (= (evaluate "return 2 * 5; 9;") 10))
    (is (= (evaluate "9; return 2 * 5; 9;") 10))
    (is (= (evaluate "if (10 > 1) { if (10 > 1) {return 10;} return 1;}") 10))
    (is (= (evaluate "let f = fn(x) { return x;  x + 10;};f(10);"), 10))
    (is (= (evaluate "let f = fn(x) { let result = x + 10; return result; return 10;};f(10);"), 20)))
  (testing "Error Handling"
    (is (= (evaluate "5 + true;") "Unknown Operator: integer + boolean"))
    (is (= (evaluate "5 + true; 5;") "Unknown Operator: integer + boolean"))
    (is (= (evaluate "-true") "Unknown Operator: - boolean"))
    (is (= (evaluate "true + false;") "Unknown Operator: boolean + boolean"))
    (is (= (evaluate "5; true + false; 5") "Unknown Operator: boolean + boolean"))
    (is (= (evaluate "if (10 > 1) { true + false; }") "Unknown Operator: boolean + boolean"))
    (is (= (evaluate "if (10 > 1) { if (10 > 1) { return true + false; } return 1; }") "Unknown Operator: boolean + boolean"))
    (is (= (evaluate "foobar") "Identifier not found: foobar"))
    (is (= (evaluate "{\"name\": \"Monkey\"}[fn(x) { x }];") "Unusable as hash key: fn"))
    (is (= (evaluate "999[1];") "Index operator not supported: integer")))
  (testing "Let Statements"
    (is (= (evaluate "let a = 5; a;") 5))
    (is (= (evaluate "let a = 5 * 5; a;") 25))
    (is (= (evaluate "let a = 5; let b = a; b;") 5))
    (is (= (evaluate "let a = 5; let b = a; b;") 5))
    (is (= (evaluate "let a = 5; let b = a; b;") 5))
    (is (= (evaluate "let a = 5; let b = a; let c = a + b + 5; c;") 15)))
  (testing "Function Application"
    (is (= (evaluate "let identity = fn(x) { x; }; identity(5);") 5))
    (is (= (evaluate "let identity = fn(x) { return x; }; identity(5);") 5))
    (is (= (evaluate "let double = fn(x) { x * 2; }; double(5);") 10))
    (is (= (evaluate "let add = fn(x, y) { x + y; }; add(5, 5);") 10))
    (is (= (evaluate "let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));") 20))
    (is (= (evaluate "fn(x) { x; }(5)") 5))
    (is (= (evaluate "let add = fn(a, b) { a + b }; let sub = fn(a, b) { a - b }; let applyFunc = fn(a, b, func) { func(a, b) }; applyFunc(2, 2, add);"), 4))
    (is (= (evaluate "let newAdder = fn(x) { fn(y) { x + y } }; let addTwo = newAdder(2); addTwo(3);"), 5))
    (is (= (evaluate "let makeGreeter = fn(greeting) { fn(name) { greeting + \" \" + name + \"!\" } }; let hello = makeGreeter(\"Hello\"); hello(\"Thorsten\");"), "Hello Thorsten!")))
  (testing "Enclosed environment"
    (is (= (evaluate "let first = 10;let second = 10;let third = 10;let ourFunction = fn(first) {  let second = 20;  first + second + third;};ourFunction(20) + first + second;") 70)))
  (testing "Closures"
    (is (= (evaluate "let newAdder = fn(x) { fn(y) { x + y } }; let addTwo = newAdder(2); addTwo(3);"), 5)))
  (testing "String literal"
    (is (= (evaluate "\"Hello World!\"") "Hello World!")))
  (testing "String Concatenation"
    (is (= (evaluate "\"Hello\" + \" \" + \"World!\"") "Hello World!")))
  (testing "Builtin Functions"
    (is (= (evaluate "len(\"\")") 0))
    (is (= (evaluate "len(\"four\")") 4))
    (is (= (evaluate "len(\"hello world\")") 11))
    (is (= (evaluate "len(1)") "len not implemented for integer"))
    (is (= (evaluate "len([1, 2, 3])") 3))
    (is (= (evaluate "len([])") 0))
    (is (= (with-out-str (evaluate "puts(\"hello world!\")")) (with-out-str (println "hello world!"))))
    (is (= (evaluate "first([1, 2, 3])") 1))
    (is (= (evaluate "first([])") nil))
    (is (= (evaluate "last([1, 2, 3])") 3))
    (is (= (evaluate "last([])") nil))
    (is (= (evaluate "rest([1, 2, 3])") [(object/integer 2) (object/integer 3)]))
    (is (= (evaluate "rest([])") nil))  
    (is (= (evaluate "push([], 1)") [(object/integer 1)]))
    (is (= (evaluate "let x = [1,2,3]; push!(x, 5);") nil))
    (is (= (evaluate "let x = [1,2,3]; push!(x, 5); x") [(object/integer 1) (object/integer 2) (object/integer 3) (object/integer 5)])))
  (testing "Array literals"
    (is (= (evaluate "[1, 2 * 2, 3 + 3]"), (mapv #(object/integer %) [1, 4, 6]))))
  (testing "Array Index Expression"
    (is (= (evaluate "[1, 2, 3][0]") 1))
    (is (= (evaluate "[1, 2, 3][1]") 2))
    (is (= (evaluate "[1, 2, 3][2]") 3))
    (is (= (evaluate "let i = 0; [1][i];") 1))
    (is (= (evaluate "[1, 2, 3][1 + 1];") 3))
    (is (= (evaluate "let myArray = [1, 2, 3]; myArray[2];") 3))
    (is (= (evaluate "let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];") 6))
    (is (= (evaluate "let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]") 2))
    (is (= (evaluate "[1, 2, 3][3]") nil))
    (is (= (evaluate "[1, 2, 3][-1]") nil)))
  (testing "Hash Literals"
    (is (= (evaluate "let two = \"two\";	{\"one\": 10 - 9,two: 1 + 1,\"thr\" + \"ee\": 6 / 2,4: 4,true: 5,false: 6}[false]") 6))
    (is (= (evaluate "let x = {foo!: 3, true: 2, \"a\": 1}; x[\"foo!\"]") 3)))
  (testing "Hash Index Expression"
    (is (= (evaluate "{\"foo\": 5}[\"foo\"]") 5))
    (is (= (evaluate "{\"foo\": 5}[\"bar\"]") nil))
    (is (= (evaluate "let key = \"foo\"; {\"foo\": 5}[key]") 5))
    (is (= (evaluate "{}[\"foo\"]") nil))
    (is (= (evaluate "{5: 5}[5]") 5))
    (is (= (evaluate "{true: 5}[true]") 5))
    (is (= (evaluate "{false: 5}[false]") 5)))
  (testing "Tail Recursion"
    (is (= (evaluate "let sumTo = fn (n, acc) { if (n == 0) { return acc; }; return sumTo(n-1, acc+n); }; sumTo(10000, 0);") 50005000)))
  (testing "null literals"
    (is (= (evaluate "let x = []; x[1]") nil)))
  (testing "if has its won enclosing scope"
    (is (= (evaluate "if (false) { } else { let x = 0; }; x;") "Identifier not found: x"))
    (is (= (evaluate "if (true) { let x = 0; }; x;") "Identifier not found: x")))
  (testing "assign operator"
    (is (= (evaluate "let x = 4; x = 3; x == 3;") true))
    (is (= (evaluate "let x = 0; let y = fn () { x = x + 1 }; y(); y(); x == 2;") true))
    (is (= (evaluate "let x = {a: 1}; x[\"a\"] = 5; x == {a: 5};") true))
    (is (= (evaluate "let x = {a: 1}; x.a = 5; x == {a: 5};") true))
    (is (= (evaluate "let x = [1, 2]; x[0] = 5; x == [5, 2];") true))
    (is (= (evaluate "let x = [1, 2]; x[10] = 5; x == [0, 5];") "Index 10 out of bounds")))
  (testing "while expr"
    (is (= (evaluate "let x = 0; while (x < 5) { x = x + 1; }; x;") 5))
    (is (= (evaluate "let x = 0; while (x < 15) { if (x < 10) { x = x + 1; continue; }; break; }; x;") 10))
    (is (= (evaluate "let x = 0; while (x < 15) { if (x > 10) { break; }; x = x + 1; }; x;") 11)))
  (testing "for expr"
    (is (= (evaluate "let x = 0; for (let x = 0; x < 5; x = x + 1) { }; x;") 5))
    (is (= (evaluate "let x = 0; for (let x = 0; x < 15; x = x + 1) { if (x < 10) { continue; }; break; }; x;") 10))
    (is (= (evaluate "let x = 0; for (let x = 0; x < 15; x = x + 1) { if (x > 10) { break; }; }; x;") 11)))
  (testing "import expr"
    (is (= (with-out-str (evaluate "let hello = import(\"examples/hello\"); hello.main()")) (with-out-str (println "Hello Monkey!")))))
  (testing "float literals"
    (is (= (evaluate "let x = 1.25; x;") 1.25)))
  (testing "hex literals"
    (is (= (evaluate "let x = 0xff; x;") 255)))
  (testing "octal literals"
    (is (= (evaluate "let x = 01060; x;") 560)))
  (testing "binary literals"
    (is (= (evaluate "let x = 0b001111; x;") 15))))
