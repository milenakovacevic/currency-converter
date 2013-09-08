(ns org.stuff.converter.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.application :only [defapplication]]
	[clojure.xml]
  )
)

(defn currency-data []
	(filter
		(fn [x] (and (= :Cube (:tag x)) (not(nil? (:currency (:attrs x))))))
		(xml-seq (parse "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"))
	)
)
(defn currency-rates []
	(zipmap	(conj (for [ x (currency-data)] (:currency (:attrs x))) "EUR") (conj (for [ x (currency-data)] (read-string(:rate (:attrs x)))) 1))
)
	
(defn convert [from to amount]
	(*(/ (get (currency-rates) to) (get (currency-rates) from)) amount)
)

(defactivity org.stuff.converter.MyActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! a
      (make-ui [:linear-layout {}
                [:text-view {:text  (convert "EUR" "USD" 10)}]])))))
