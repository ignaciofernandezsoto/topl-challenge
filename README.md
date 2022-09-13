# Topl Challenge

## Instructions
This is a programming exercise that involves reading a data file that contains information about the traffic flow on some fictitious city streets. To keep this simple, the streets are all one-way. The program is to be written in Scala using a functional style of programming.

The streets are mostly in a grid pattern. Streets identified by numbers (1, 2, 3, ...) run from east to west or from west to east with Street 1 being the northernmost street. Avenues identified by letters (A, B, C, ...) run from north to south or from south to north, with avenue A being the westernmost avenue. There is also an elevated expressway that connects more than two intersections and does not follow the grid pattern.

The program will need to read the data and then find good routes between different pairs of intersections. Intersections are identified by the names of the avenue and street that intersect at the intersection. For example, an intersection may be identified by the combination of Avenue F and street 24.

The input to the program will consist of the data file and two intersections. It must be possible to specify the location of the data file and the names of the two intersections on the command line. The program output will describe a good route to travel from the first intersection to the second.

## Features
- Strategy to either do an average or a weighted average with the transit times across the measurements
- Multiple unit tests (80%~ code coverage)
- Able to be run on Docker
- GitHub CI

## How it works
1) The request is parsed and validated. If it badly formatted or lacks an argument, an error is returned.
2) If the request is valid, then the sample data file is retrieved. If it doesn't exist, or it's wrongly formatted, an error is returned.
3) If the sample data was retrieved correctly, then it's grouped by measured roads. As the data may contain different measurements through time, all measurements are grouped by roads for ease to operate on them.
4) Once the measurements are grouped by cross-road, the average time for each cross-road is calculated.
   - If the user selected or was defaulted the average strategy, then the algorithm sums all transit times and divides them with the total count. 
     - For instance, if there are three measurements with the values 2, 8 and 20, then 2 + 8 + 20 is 30, and as there are three measurements, it's divided by 3. The average is 10.
   - If the user selected the weighted times' strategy, then the algorithm favors the most recent measured transit time. The algorithm counts the total amount of measurements and sums one to it. Then, the total assignable weight is 100, and it is divided by the previous total count plus one that was calculated. This returns a percentage that should be multiplied to each transit time to get the average, but there's a catch. It was calculated for one element more than the ones that came. To make the averages sum correctly, the most recent measured transit time gets the remaining percentage. 
     - For instance, if there are three measurements with the values 2, 8 and 20, with 20 being the most recent transit time. There are three measurements, but we'll pretend there are four. 100 / 4 is equal to 25. But, in reality there were three measurements, so 100 / 3 is 33. We'll do 2 * 25, 8 * 25 and 20 * (100 - 50). After that, we'll sum everything and divide it by 100. The weighted average is 12.5.
5) After the averages are calculated, a graph is made. The roads measurements are converted to intersections.
6) Once the graph is done, the shortest path algorithm is executed. I chose Dijkstra.
7) After the algorithm finishes the execution, its response is transformed to a more human friendly one, and it gets encoded into a Json.
8) The Json is returned and the application exits. 

## How to use

#### Requirements
- SBT
- Docker

#### How to run
- `sbt docker:publishLocal`
- `docker run -v {sample data file location}:/mnt/mydata --rm topl-challenge:0.1.0-SNAPSHOT {avenue,street} {avenue,street} /mnt/mydata/sample-data.json {transit time average strategy [OPTIONAL, defaults to AVERAGE]}`

#### Run example

`docker run -v /Users/ijfs/Downloads:/mnt/mydata --rm topl-challenge:0.1.0-SNAPSHOT A,1 C,1 /mnt/mydata/sample-data.json weighted_average`

#### Argument format
- Avenue: Single alphabetical character. It's case-sensitive.
- Street: Whole non-negative number.
- Average Strategy: Accepts either AVERAGE or WEIGHTED_AVERAGE values. Optional (defaults to AVERAGE). It's not case-sensitive.

#### Return example
```json
{
   "transitsAverageStrategy": "WEIGHTED_AVERAGE",
   "startingIntersection": {
      "avenue": "A",
      "street": "1"
   },
   "endingIntersection": {
      "avenue": "C",
      "street": "1"
   },
   "paths": [
      {
         "initialNode": {
            "avenue": "A",
            "street": "1"
         },
         "finalNode": {
            "avenue": "B",
            "street": "1"
         },
         "distance": 34.66181689446774
      },
      {
         "initialNode": {
            "avenue": "B",
            "street": "1"
         },
         "finalNode": {
            "avenue": "C",
            "street": "1"
         },
         "distance": 23.530389580113816
      }
   ],
   "totalTransitTime": 58.19220647458155
}
```

- It's an ordered list of paths.

## Technical Debt
- Use [Refined](https://github.com/fthomas/refined/issues/932) (once it's correctly supported on Scala 3).
- Use [Scala Mock](https://github.com/mockito/mockito-scala/issues/364) (once it's correctly supported on Scala 3).
- Use [sbt-scoverage](https://github.com/sbt/sbt/issues/6997) (once it's correctly supported on Scala 3).
- Better weighted times strategy algorithm.