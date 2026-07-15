(ns association.facts
  "Industry rule/policy-statement catalog for the American Chemistry
  Council (ACC, Wikidata Q4743356) -- a 26th industry-association-
  level source (see cloud-itonami-assoc-6419-jpn-zenginkyo, -6512-jpn-sonpo,
  -6612-jpn-jsda, -6419-deu-bankenverband, -6612-usa-finra, -6512-usa-naic,
  -6920-jpn-jicpa, -6920-usa-aicpa, -6419-fra-fbf, -6511-jpn-seiho,
  -6910-jpn-nichibenren, -6810-jpn-recaj, -6411-jpn-boj, -6120-usa-ctia,
  -5110-usa-a4a, -3510-usa-eei, -2910-deu-vda, -5510-usa-ahla,
  -2100-usa-phrma, -4719-usa-nrf, -4100-usa-agc, -6020-usa-nab,
  -3600-usa-awwa, -4923-usa-ata, -5610-usa-nra for the first
  twenty-five) per ADR-2607141700 (cloud-itonami-compliance-fact-federation).
  The FIRST entry aligned to ISIC 2011 (manufacture of basic
  chemicals) -- a new industry code for this family. A rule not in
  this table has NO spec-basis, full stop; extend `catalog`, do not
  invent an id/url/date.

  Both entries were directly WebFetch-verified against
  americanchemistry.com's own pages. Neither page states a specific
  month/day for its date -- 'Our 150 Years History' confirms ACC's
  founding year (1872, originally as the Manufacturing Chemist
  Association of the United States) but no month/day anywhere found;
  'Responsible Care Overview' confirms the Responsible Care
  safety/sustainability program was 'Launched in the U.S. in 1988' but
  likewise gives no month/day. Both :established-date values are
  therefore deliberately year-only rather than invented full dates.")

(def catalog
  "assoc-slug -> vector of self-regulatory rule entries."
  {"acc"
   [{:association-rule/id "acc.our-150-years-history"
     :association-rule/title "Our 150 Years History"
     :association-rule/association "acc"
     :association-rule/isic "2011"
     :association-rule/country "USA"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.americanchemistry.com/about-acc/our-150-years-history"
     :association-rule/url-provenance :official-association-site
     :association-rule/established-date "1872"
     :association-rule/retrieved-at "2026-07-16"
     :association-rule/topic #{:governance}}
    {:association-rule/id "acc.responsible-care-overview"
     :association-rule/title "Responsible Care Overview"
     :association-rule/association "acc"
     :association-rule/isic "2011"
     :association-rule/country "USA"
     :association-rule/kind :self-regulatory-code
     :association-rule/url "https://www.americanchemistry.com/driving-safety-sustainability/responsible-care-driving-safety-sustainability/resources/responsible-care-overview"
     :association-rule/url-provenance :official-association-site
     :association-rule/established-date "1988"
     :association-rule/retrieved-at "2026-07-16"
     :association-rule/topic #{:safety :environment}}]})

(defn spec-basis [assoc-slug] (get catalog assoc-slug))

(defn coverage
  ([] (coverage (keys catalog)))
  ([slugs]
   (let [have (filter catalog slugs)
         missing (remove catalog slugs)]
     {:requested (count slugs)
      :covered (count have)
      :covered-associations (vec (sort have))
      :missing-associations (vec (sort missing))
      :note (str "cloud-itonami-assoc-2011-usa-acc Wave 0 (ADR-2607141700): "
                 (count (get catalog "acc")) " acc entries seeded with an "
                 "official americanchemistry.com citation. Extend "
                 "`association.facts/catalog`, never fabricate a rule id/url.")})))

(defn by-topic [assoc-slug topic]
  (filterv #(contains? (:association-rule/topic %) topic) (spec-basis assoc-slug)))
