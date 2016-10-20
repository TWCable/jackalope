# RELEASE NOTES

## 3.0.2

Bug fixes:

* Preserve the order of nodes in the repository to match the order they were created, so that tests can count on node
  order when using functions such as page.listChildren() or node.getNodes()
* Fix bug where "deep" parameter to page.listChildren() is being ignored
* Fix bug page.listChildren() hits NPE due to having a null filter
* Fix bug where PageImpl.isHideInNav() throws NPE if hideInNav property is not set
* Fix bug where PageImpl.isValid() returns false for valid pages, incorrectly returning true for pages that have a
  future onTime instead of a past onTime and future offTime

## 3.0.1

Implemented ResourceResolverImpl delete(..) and create(..) methods

## 3.0.0

Updated to support AEM 6.0, from AEM 5.6.1

## 2.0.0

Initial public release
