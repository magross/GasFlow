# GasFlow
Graph-based models for gas pipeline systems, with some functionality for water and power grids. Contains data structures, input/output functionality, algorithms to simulate and control gas propagation, and preprocessing to reduce the size of gas/water/power networks to speed up the application of MILP/MINLP solvers.

The preprocessing exploits the fact that [generalized series-parallel graphs](https://en.wikipedia.org/wiki/Series-parallel_graph) can easily be compressed into simpler structures (in some situations to a single edge).

## File Formats

GasFlow supports the XML-based file formats from [GasLib](http://gaslib.zib.de/), Anaconda XML, GML, DAT and GMSDAT support. The corresponding classes can be found in the [`gas/io/gaslib`](https://github.com/magross/GasFlow/tree/master/src/gas/io/gaslib), [`gas/io/anaconda`](https://github.com/magross/GasFlow/tree/master/src/gas/io/anaconda), [`gas/io/gml`](https://github.com/magross/GasFlow/tree/master/src/gas/io/gml) and [`gas/io/water`](https://github.com/magross/GasFlow/tree/master/src/gas/io/water) packages.

## Visualization

Networks can be visualized as vector-graphics using [PGF/TikZ](https://en.wikipedia.org/wiki/PGF/TikZ). The support for this feature is located in [`gas/io/tikz`](https://github.com/magross/GasFlow/tree/master/src/gas/io/tikz) 

## Graph Generators

For testing purposes, this project includes generators for generalized series-parallel graphs in the [`ds.graph.generator`](https://github.com/magross/GasFlow/tree/master/src/ds/graph/generator) package. 

## Size Reduction by Bound Propagation

The size reduction of the instances exploits structures of the graphs which are generalized series-parallel (which occurs often in reality in the kind of networks that we are considering), and propagates bounds through the network. The result is an reduction of the instance size of up to 76%.
