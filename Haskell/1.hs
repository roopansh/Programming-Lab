import Data.List
import Data.Char
import Data.String
import Data.Ord

{-
    absValue
    @param n input number
    outputs the absolute value of the input
-}
absValue :: Int -> Int
absValue n = if n >= 0 then n else -n

{-
	palindrome
	If empty input, then output 0
	If only one character in the input, then again output 0
	Otherwise, take the first and last character, calculate the difference between them, and recurse into the remaining string in between.
-}
palindrome :: String -> Int
palindrome [] = 0
palindrome [singleChar] = 0
palindrome input = absValue((ord (head input)) - (ord (last input)))  + (palindrome (tail (init input)))
