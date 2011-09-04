Clj3D - Visual prototyping for the masses
=========================================

Clj3D is a Clojure graphic library for manipulating 3D and 2D objects. It
aims to be the Clojure standard in 3D and 2D rendering. 

.. image:: https://github.com/CharlesStain/clj3D/raw/makai/imgs/screen2.jpg
.. image:: https://github.com/CharlesStain/clj3D/raw/makai/imgs/screen4.jpg
.. image:: https://github.com/CharlesStain/clj3D/raw/makai/imgs/leonard.jpg
.. image:: https://github.com/CharlesStain/clj3D/raw/makai/imgs/skyscraper.jpg


Read Carefully!!
================

I'm not going to update the library, at least for the moment. By the way, if you
are interested in contributing, you should be sure to make che library compatible for
the coming 1.3 stable version of Clojure. In order to accomplish that, we should follow
the instruction given us from the Clojure Google Group, that I'll report here:

  - *Try migrating your lib to 1.3* 
      - Create a 1.3 branch 
      - Remove earmuffs around any non-rebound vars 
      - Add earmuffs to any vars that are rebound using thread-level binding 
      - Add ^:dynamic to these vars 
      - If you rely on the built in Numerics, check to see if the new 
      changes<http://dev.clojure.org/display/doc/Documentation+for+1.3+Numerics>in 1.3 affect you. 
   - *Do some house cleaning* 
      - If you are no longer maintaining this library, simply note so at the 
      top of your Readme. If the reason is that a better alternative has spring 
      up, link to it. 
      - Take a look at your dev dependencies and determine if any of them 
      should remain in light of the ability to globally install leiningen plugins. 
      *If you have swank-clojure as a dependency, please remove it*: this has been the source of numerous issues.
      
It seems to be a lot of work to do, but I have no time. I report this disclaimer here hoping that someone
soon or later will contribute.
Thanks,
Alfredo


**The second screencast is out! Watch it** `here <http://www.youtube.com/watch?v=Xg2gZpWU6AE>`_

**The first screencast is out! Watch it** `here <http://www.youtube.com/watch?v=_fLgBzRdddU>`_

You should consider using Clj3D for a few reasons..

..Clj3D is jMonkeyEngine and Plasm reloaded
-------------------------------------------
Clj3D is massively based on the excellent `jMonkeyEngine <http://jmonkeyengine.org/>`_ 
and it tooks the best of it. `PLaSM <http://www.dia.uniroma3.it/~paoluzzi/plasm502/>`_
is a "design language" for geometric and solid parametric design, developed by the 
CAD Group at the Universities "La Sapienza" and "Roma Tre".\

..Clj3D is easy to start with
-----------------------------
No external dependencies, no modules to install. Just a single jar:
::

[org.clojars.charles-stain/clj3d "0.0.4"]

Import it into a Leiningen project and you are ready to go. Displaying something is dead-easy:
::

    (use '(clj3D math fl fenvs viewer) :reload)
    (view (cube 1))

..Clj3D is batteries included
-----------------------------
Clj3D is shipped with some interesting library and features, like the powerful
`Incanter <http://incanter.org/>`_ library. Clj3D offers to you the best features
of this library directly injected into the namespace. Forget external ":use" or
":require". You can even use some Incanter methods:
::

  (view (matrix [[0 1 2] 
                 [3 4 5]]))

..Clj3D is fast (as it can)
---------------------------
Clojure is not (unfortunately) written in C, but Java can be relatively fast. Clj3D
tries to be as fast as Java native code, using vectors as primary data structures and
Incanter's matrix when they needs.

..Clj3D is free
---------------
Clj3D is free and it's hosted on `Github <https://github.com/CharlesStain/clj3D>`_

Installation and usage
----------------------
I will provide monthly jar on Clojars. These standalone jars can be imported directly into a leiningen project.
Alternatively, you can clone this repo and create the jar from scratch. You can even play with clj3D in the usual way:
::

  git clone
  lein deps
  lein compile
  lein repl
  ;=>PLAY! HAVE FUN!

**For usage consult the** `Wiki <https://github.com/CharlesStain/clj3D/wiki>`_

License
-------

Copyright (C) 2011 Alfredo Di Napoli

Distributed under the MIT License. JMonkey3 is released under the BSD License. See Wiki.
