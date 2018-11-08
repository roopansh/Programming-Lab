import Data.List

{-
    Add 1 to a given number
-}
add1 :: Int -> Int
add1 x = x + 1

{-
    Add 1 to every number of a given list
-}
addList1 :: [Int] -> [Int]
addList1 numbers = map add1 numbers

{-
    If the minimum and maximum salary are same, then no more operation required
    otherwise,
        1) sort the salaries list
        2) take the first len-1 elements
        3) add 1 to every element
        4) recurse with the current salaries
        5) return 1 + the result from recursion
-}
salary :: Int -> [Int] -> Int
salary len salaries
    |   maximum salaries == minimum salaries    = 0
    |   otherwise   =   1 + (salary len ((addList1 (take (len-1) (sort salaries))) ++ ( (maximum salaries) : [] )))
