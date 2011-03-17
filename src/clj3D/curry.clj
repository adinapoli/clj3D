(ns clj3D.curry)

(defn partial+
  "Takes a function f and fewer than the normal arguments to f, and
  returns a fn that takes a variable number of additional args. When
  called, the returned function calls f with args + additional args.
  differs from the core version in that it works on just one argument."
  {:added "1.0"}
  ([f] f)
  ([f arg1]
   (fn [& args] (apply f arg1 args)))
  ([f arg1 arg2]
   (fn [& args] (apply f arg1 arg2 args)))
  ([f arg1 arg2 arg3]
   (fn [& args] (apply f arg1 arg2 arg3 args)))
  ([f arg1 arg2 arg3 & more]
   (fn [& args] (apply f arg1 arg2 arg3 (concat more args)))))


(defmacro defn-decorated
  "like defn except it accepts an additional vector of
   decorator functions which will be applied to the base definition.
   the decorators are applied in left-to-right order."
  {:author "Robert McIntyre"
   :arglists '[[name [modifers*] doc-string? attr-map? [params*] body]
	       [name [modifers*] doc-string? attr-map? ([params*] body) + attr-map?]]}
  [fn-name decorators & defn-stuff]
  `(do
     (defn ~fn-name ~@defn-stuff)
     (alter-var-root (var ~fn-name) (reduce comp identity (reverse ~decorators)))
     (var ~fn-name)))


(defmacro defhigh
  "Like the original defn-decorated, but the number of argument to curry on
  is implicit."
  [fn-name & defn-stuff]
  (let [[fst snd] (take 2 defn-stuff)
         num-of-args (if (string? fst) (count snd) (count fst))]
    `(defn-decorated ~fn-name [(curry* ~num-of-args)] ~@defn-stuff)))


(defn curry**
  [number-of-args f]
  (fn
    ([& args]
       (let [number-of-inputs (count args)]
	 (if (= number-of-inputs number-of-args)
	   (apply f args)
	   (curry** (- number-of-args number-of-inputs)
		    (apply (partial+ partial+ f) args)))))))

(def curry* (curry** 2 curry**))

(defn-decorated
  curry-on
  [(curry* 2)]
  "higher order function that enables automatic curying as in haskel, scheme"
  {:author "Robert McIntyre"}
  [number-of-args f]
  (curry** number-of-args f))


;;; Example of Use

(defn-decorated
  demo
  [memoize (curry-on 3)]
  "I like the vector of unitarty higher order transforms ---
   sort of like a list of modifiers on a magic(tm) card.
   This function has flying, resistance to black, etc :)"
  [a b c]
  (println "side effect")
  (+ a b c))


;; (((((demo1)) 1) 2) 3)
;; 6

