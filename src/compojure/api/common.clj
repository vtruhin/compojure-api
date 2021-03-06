(ns compojure.api.common)

(defn plain-map?
  "checks whether input is a map, but not a record"
  [x] (and (map? x) (not (record? x))))

(defn extract-parameters
  "Extract parameters from head of the list. Parameters can be:

  1. a map (if followed by any form) `[{:a 1 :b 2} :body]` => `{:a 1 :b 2}`
  2. list of keywords & values `[:a 1 :b 2 :body]` => `{:a 1 :b 2}`
  3. else => `{}`

  Returns a tuple with parameters and body without the parameters"
  [c expect-body]
  (cond
    (and (plain-map? (first c)) (or (not expect-body) (seq (rest c))))
    [(first c) (seq (rest c))]

    (keyword? (first c))
    (let [parameters (->> c
                          (partition 2)
                          (take-while (comp keyword? first))
                          (mapcat identity)
                          (apply array-map))
          form (drop (* 2 (count parameters)) c)]
      [parameters (seq form)])

    :else
    [{} (seq c)]))

(defn group-with
  "Groups a sequence with predicate returning a tuple of sequences."
  [pred coll]
  [(seq (filter pred coll))
   (seq (remove pred coll))])
