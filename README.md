### Collage Tool

This is an Eclipse Processing3 project which means:

[Processing in Eclipse](https://processing.org/tutorials/eclipse/)

**Ideas**

* Undo button
* Automatically handle rectangular images (not sure resampling is worth it)
* Drag selection
* manipulations

	```
		// y-reflect
		a b c d --> d c b a
		
		// y-reflect
		a b 	 --> b a
		c d  --> d c
		
		// do similar for x
	```
* reassign squares within the target grid (in fact, all the operations of the source grid should also be possible for the target grid)

**TODO**

* Fix selection button
* show last selection and allow continued pasting until source grid is selected again.
* show which square will be used as the reference square

**References**

* [Timing](https://processing.org/reference/millis_.html)
* [Tables](https://processing.org/reference/Table.html)
* [Processing + Maven + Eclipse](http://jtoprocessing.tumblr.com/post/63945371987/how-to-processing-maven-eclipse)
* [How not to display the class in git](https://stackoverflow.com/questions/14251253/how-not-to-display-the-class-in-git) - StackOverflow
* [Show Hidden Files in Eclipse](http://cesaric.com/?p=591) - cesaric.com
* [The Builder Pattern](http://www.vogella.com/tutorials/DesignPatternBuilder/article.html) - vogella.com
* [Vim: Remove unwanted spaces](http://vim.wikia.com/wiki/Remove_unwanted_spaces) - vim.wikia
* [Java 8 DateTime examples](https://gist.github.com/mscharhag/9195718) - mscharhag GitHub
* [Git: diff file against its last change](https://stackoverflow.com/questions/10176601/git-diff-file-against-its-last-change) - StackOverflow - `git diff remotes/origin/master master`

* [Git: Compare Local Branch with Remote Branch](https://stackoverflow.com/questions/1800783/compare-local-git-branch-with-remote-branch)
* [Vim: Search for a Pattern and if occurs Delete to End of Line](https://stackoverflow.com/questions/569280/vim-search-for-a-pattern-and-if-occurs-delete-to-end-of-line) - StackOverflow