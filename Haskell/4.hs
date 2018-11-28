import Data.Function

{-
    Check all the possible values for the X dimension of the bedroom and returning most optimal one.
-}
checkBedroom :: [Int]->[[Int]]->Int->[[Int]]
checkBedroom roomCount roomSize totalSpace =
    if(((roomSize!!0)!!0) < 15)
    then do
        let design1 = checkHall roomCount roomSize totalSpace
        let design2 = checkBedroom roomCount ([[((roomSize!!0)!!0) + 1, ((roomSize!!0)!!1) + 1]] ++ (drop 1 roomSize)) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    Check all the possible values for the X dimension of the hall and returning most optimal one.
-}
checkHall :: [Int]->[[Int]]->Int->[[Int]]
checkHall roomCount roomSize totalSpace =
    if(((roomSize!!1)!!0) < 20)
    then do
        let design1 = checkKitchen roomCount roomSize totalSpace
        let design2 = checkHall roomCount ( (take 1 roomSize) ++ [[((roomSize!!1)!!0) + 1,((roomSize!!1)!!1) + 1]] ++ (drop 2 roomSize) ) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    Check all the possible values for the X dimension of the kitchen and returning most optimal one.
-}
checkKitchen :: [Int]->[[Int]]->Int->[[Int]]
checkKitchen roomCount roomSize totalSpace =
    if(((roomSize!!2)!!0) < 15)
    then do
        let design1 = checkBathroom roomCount roomSize totalSpace
        let design2 = checkKitchen roomCount ( (take 2 roomSize) ++ [[((roomSize!!2)!!0) + 1,((roomSize!!2)!!1) + 1]] ++ (drop 3 roomSize) ) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    Check all the possible values for the X dimension of the bathroom and returning most optimal one.
-}
checkBathroom :: [Int]->[[Int]]->Int->[[Int]]
checkBathroom roomCount roomSize totalSpace =
    if(((roomSize!!3)!!0) < 8)
    then do
        let design1 = checkBalcony roomCount roomSize totalSpace
        let design2 = checkBathroom roomCount ( (take 3 roomSize) ++ [[((roomSize!!3)!!0) + 1,((roomSize!!3)!!1) + 1]] ++ (drop 4 roomSize) ) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    Check all the possible values for the X dimension of the balcony and returning most optimal one.
-}
checkBalcony :: [Int]->[[Int]]->Int->[[Int]]
checkBalcony roomCount roomSize totalSpace =
    if(((roomSize!!4)!!0)<10)
    then do
        let design1 = checkGarden roomCount roomSize totalSpace
        let design2 = checkBalcony roomCount ( (take 4 roomSize) ++ [[((roomSize!!4)!!0) + 1,((roomSize!!4)!!1) + 1]] ++ (drop 5 roomSize) ) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    Check all the possible values for the X dimension of the gardens and returning most optimal one.
-}
checkGarden :: [Int]->[[Int]]->Int->[[Int]]
checkGarden roomCount roomSize totalSpace =
    if(((roomSize!!5)!!0) < 20)
    then do
        let design1 = checkFinal roomCount roomSize totalSpace
        let design2 = checkGarden roomCount ( (take 5 roomSize) ++ [[((roomSize!!5)!!0) + 1,((roomSize!!5)!!1) + 1]]) totalSpace
        compareDesigns design1 design2 roomCount
    else
        [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

{-
    checking that the given configuration(int roomSize) satisfies all constraints
-}
checkFinal :: [Int]->[[Int]]->Int->[[Int]]
checkFinal roomCount roomSize totalSpace
    -- If the total space if not sufficient
    |   calc_area roomCount roomSize > totalSpace                                           = [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

    -- If the Kitchen size if greater than hall size
    |   (((roomSize!!2)!!0) * ((roomSize!!2)!!1) > ((roomSize!!1)!!0) * ((roomSize!!1)!!1)) = [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

    -- If the Kitchen size if greater than bedroom size
    |   (((roomSize!!2)!!0) * ((roomSize!!2)!!1) > ((roomSize!!0)!!0) * ((roomSize!!0)!!1)) = [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

    -- If the bathroom is larger than the Kitchen
    |   (((roomSize!!3)!!0) * ((roomSize!!3)!!1) > ((roomSize!!2)!!0) * ((roomSize!!2)!!1)) = [ [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0] ]

    -- Otherwise, it is feasible
    |   otherwise = roomSize


{-
    compareDesignsare two answers based on which has better space utilization
-}
compareDesigns :: [[Int]]->[[Int]]->[Int]->[[Int]]
compareDesigns design1 design2 roomCount =
    if(calc_area roomCount design1 > calc_area roomCount design2) then
        design1
    else
        design2

{-
    calculate totalSpace used for given configuration.
-}
calc_area :: [Int]->[[Int]]->Int
calc_area roomCount roomSize = bedroom_area + hall_area + kitchen_area + bathrooms_area + garden_area + balcony_area
    where
        bedroom_area    = (roomCount!!0) * ((roomSize!!0)!!0) * ((roomSize!!0)!!0)
        hall_area       = (roomCount!!1) * ((roomSize!!1)!!0) * ((roomSize!!1)!!1)
        kitchen_area    = (roomCount!!2) * ((roomSize!!2)!!0) * ((roomSize!!2)!!1)
        bathrooms_area  = (roomCount!!3) * ((roomSize!!3)!!0) * ((roomSize!!3)!!1)
        garden_area     = (roomCount!!4) * ((roomSize!!4)!!0) * ((roomSize!!4)!!1)
        balcony_area    = (roomCount!!5) * ((roomSize!!5)!!0) * ((roomSize!!5)!!1)


showsArc :: [[Int]] -> [Int] -> Int -> ShowS
showsArc result roomCount unusedSpace = showString "Bedroom: (" . shows (roomCount!!0) . showString ") ". shows ((result!!0)!!0) . showString "X" .  shows ((result!!0)!!1) . showString "," . showString "Hall: (" . shows (roomCount!!1) . showString ") ". shows ((result!!1)!!0) . showString "X" .  shows ((result!!1)!!1) . showString "," . showString "Kitchen: (" . shows (roomCount!!2) . showString ") ". shows ((result!!2)!!0) . showString "X" .  shows ((result!!2)!!1) . showString "," . showString "Bathroom: (" . shows (roomCount!!3) . showString ") ". shows ((result!!3)!!0) . showString "X" .  shows ((result!!3)!!1) . showString "," . showString "Balcony: (" . shows (roomCount!!4) . showString ") ". shows ((result!!4)!!0) . showString "X" .  shows ((result!!4)!!1) . showString "," . showString "Garden: (" . shows (roomCount!!5) . showString ") ". shows ((result!!5)!!0) . showString "X" .  shows ((result!!5)!!1) . showString "," . showString "Unused Space:" . shows (unusedSpace)


{-
    House planner function
    @param totalspace is the total available space to plan the design
    @param bedroom is the number of bedrooms
    @param hall is the number of halls
    @return Nothings
    Prints a possible design
-}
planHouse :: Int->Int->Int->IO ()
planHouse totalSpace bedroom hall = do
    -- Only 1 garden and balcony
    let garden = 1
    let balcony = 1

    -- bathroom is one more than bedrooms
    let bathroom = bedroom + 1

    -- Every 3 bedroom has 1 kitchen
    let kitchen = ceiling ((fromIntegral bedroom) / (fromIntegral 3))

    -- Count of rooms are fixed for each type of room
    let roomCount = [bedroom, hall, kitchen, bathroom, garden, balcony]

    -- Minimum possible dimension for each type of the room
    let bedroomSize = [10, 10]
    let hallSize = [15, 10]
    let kitchenSize = [7, 5]
    let bathroomSize = [4, 5]
    let balconySize = [5, 5]
    let gardenSize = [10, 10]

    -- roomSize value
    let roomSize = [bedroomSize, hallSize, kitchenSize, bathroomSize, balconySize, gardenSize]

    -- final answer
    let result = checkBedroom roomCount roomSize totalSpace

    -- used space
    let used_space = calc_area roomCount result

    -- unused space
    let unusedSpace = totalSpace - used_space

    -- print result
    if ( ((result!!0)!!0) > 0) then
        print(showsArc result roomCount unusedSpace [])
    else
        print( showString "Not possible" [])
