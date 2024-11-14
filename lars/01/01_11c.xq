(:
Implement a linear algorithm in XQuery.

I found this clean algorithm: https://craftofcoding.wordpress.com/2021/12/10/fibonacci-by-linear-recursion-is-better/

int fib_linearR(int a, int b, int n) {x
   if (n <= 2) 
      return b;
   else if (n > 2)
      return fib_linearR(b,a+b,n−1);
}

(they are off by 1 since they assume 1,1 fib seq)

Porting this to XQuery...
:)
declare function local:linear_fib($a as xs:integer, $b as xs:integer, $n as xs:integer) as xs:integer {
  if ($n = 1) then $b
  else local:linear_fib($b, $a + $b, $n - 1)
};
declare function local:fib($n as xs:integer) as xs:integer {
  if ($n = 0) then 0
  else local:linear_fib(0, 1, $n)
};

local:fib(10)
