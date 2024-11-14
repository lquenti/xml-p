(:
Write a recursive XQuery function that computes the n-th Fibonacci Number
fib(0) := 0, fib(1) := 1
:)
declare function local:fib($n as xs:integer) as xs:integer {
  if ($n = 0) then 0
  else if ($n = 1) then 1
  else (local:fib($n - 1) + local:fib($n - 2))
};

local:fib(10)

(:
Time complexity: O(2^n)
Space Complexity: O(n) since recursion should be dfs
:)
