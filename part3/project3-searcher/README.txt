For XML attributes, our searcher escaped &, <, >, ", '. For XML text, we escaped, & and <.
For spatial search combined with basic search, we first stored all results from our spatial search
in a set and checked which elements in our keyword search is also in the spatial search via the set.