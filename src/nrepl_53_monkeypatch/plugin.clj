(ns nrepl-53-monkeypatch.plugin)

(def monkeypatch-code
  '(do (require 'clojure.tools.nrepl.middleware)
       (binding [*ns* (the-ns 'clojure.tools.nrepl.middleware)]
         (eval
          '(do
             (defn- topologically-sort
               [comparator stack]
               (let [stack (vec stack)
                     vertices (range (count stack))
                     edges (for [i1 vertices
                                 i2 (range i1)
                                 :let [x (comparator (stack i1) (stack i2))]
                                 :when (not= 0 x)]
                             (if (neg? x) [i1 i2] [i2 i1]))
                     trivial-vertices (remove (set (apply concat edges)) vertices)]
                 (loop [sorted-vertices []
                        remaining-edges edges
                        remaining-vertices (remove (set trivial-vertices) vertices)]
                   (if (seq remaining-vertices)
                     (let [non-initials (->> remaining-edges
                                             (map second)
                                             (set))
                           next-vertex (->> remaining-vertices
                                            (remove non-initials)
                                            (first))]
                       (if next-vertex
                         (recur (conj sorted-vertices next-vertex)
                                (remove #((set %) next-vertex) remaining-edges)
                                (remove #{next-vertex} remaining-vertices))
                         (let [start (first remaining-vertices)
                               step (into {} remaining-edges)
                               cycle (->> (iterate step start)
                                          (rest)
                                          (take-while (complement #{start}))
                                          (cons start))
                               data {:cycle (map stack cycle)}]
                           (throw (ex-info
                                   "Unable to satisfy nrepl middleware ordering requirements!"
                                   data)))))
                     (map stack (concat sorted-vertices trivial-vertices))))))
             (defn linearize-middleware-stack
               [middlewares]
               (->> middlewares
                    extend-deps
                    (topologically-sort comparator)
                    (map :implemented-by))))))))

(defn middleware
  [project]
  (update-in project [:injections] (fnil conj []) monkeypatch-code))
