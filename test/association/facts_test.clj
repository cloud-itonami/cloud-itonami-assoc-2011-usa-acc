(ns association.facts-test
  (:require [clojure.test :refer [deftest is]]
            [association.facts :as facts]))

(deftest acc-has-spec-basis
  (let [sb (facts/spec-basis "acc")]
    (is (= 2 (count sb)))
    (is (every? #(= "2011" (:association-rule/isic %)) sb))
    (is (every? #(= "USA" (:association-rule/country %)) sb))))

(deftest unknown-association-has-no-spec-basis
  (is (nil? (facts/spec-basis "wef")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["acc" "wef"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["wef"] (:missing-associations c)))))

(deftest by-topic-filters
  (is (= ["acc.responsible-care-overview"]
         (mapv :association-rule/id (facts/by-topic "acc" :safety))))
  (is (empty? (facts/by-topic "acc" :labor)))
  (is (empty? (facts/by-topic "wef" :governance))))
