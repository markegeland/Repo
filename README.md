Capture
=======
Welcome to the Republic Services Capture Repository!
Just starting with the Capture repository?  Read on...

## Quick Links
- [Capture Issue Tracker](https://github.com/RepublicServicesRepository/Capture/issues)
- [Great Git Tutorial](https://www.atlassian.com/git/tutorials/)

## Developers
####Installing Git
- [Install Git for Windows](http://git-scm.com/download/win) - download will start automatically
- [Install GitHub Windows Client](https://windows.github.com/) - (*optional*) installs Git Windows GUI

####Our Source Control Methodology
We've based the methodology on [this great article](http://nvie.com/posts/a-successful-git-branching-model/), so that's a pretty important read...

Based on that, there are few quick points to summarize:

1. The master branch is what we have in production.  **Please do not commit to the master branch.**
2. The develop branch is where we push code when it's ready for the next release.
3. A release branch will be created off of develop when all of the features are ready for testing.  Any testing issues found will be made on the release branch, and then merged to develop.
4. A hotfix branch will be created when we have an emergency fix for prod (results in a bump to the release version number).
5. Feature branches off of develop should be created for major features.

**A Typical Day For A Capture Developer**

1. Get coffee :coffee:
2. Open [the issues list](https://github.com/RepublicServicesRepository/Capture/issues) to figure out what to work on.  Ok, high priority issue assigned to me, let's work on that.  Add a 'development' [label](https://github.com/RepublicServicesRepository/Capture/labels) to the issue so the team knows we're actively working on it.
3. Sync up local repos with remote Capture repos. (git fetch/merge, or git pull)
4. Create a new branch for the feature. (git checkout -b new-branch-name develop)
5. Edit files to implement awesome feature. (use your favorite editor)
6. Test your changes in Capture Dev.
7. Merge feature branch into develop. (git checkout develop, git merge --no-ff new-branch-name)
8. Delete your feature branch (git branch -d new-branch-name)
9. Push your feature to capture/develop (git push capture develop)
10. :beers:

11. sleep :-)
