[![Build Status](https://travis-ci.org/vsfexperts/LaTeX.svg?branch=release-0.2.0)](https://travis-ci.org/vsfexperts/LaTeX)
[![codecov](https://codecov.io/gh/vsfexperts/LaTeX/branch/release-0.2.0/graph/badge.svg)](https://codecov.io/gh/vsfexperts/LaTeX)

# Overview
This project is providing a simple integration of LaTeX with java. Velocity templates are compiled to valid LaTeX code and the result is fed to the external pdflatex process.

# Modules
- latex-template: to be used by clients to create a valid tex file, basically just a wrapper of velocity. 
- latex-server: simple REST frontend to render latex snippets
- latex-renderer: to be used by server/local client to process the generated tex files (created by the template) in order to convert LaTeX code into pdfs

# Flow
1. Velocity tex template is processed and results are returned as String
2. Renderer creates a temp file containing the tex input and renders the pdf in the same directory
3. Renderer is archiving the pdf output and the tex input file in a separate archive directory

# Quickstart
1. Install [MiKTeX](https://miktex.org/download) on windows / [TexLive](https://launchpad.net/~jonathonf/+archive/ubuntu/texlive-2016) on Linux and make sure the `pdflatex` executable is available via system path (e.g. by executing it on the command line).
2. Browse to `src/test/resources/META-INF` and compile the `sample.tex` file by executing `pdflatex sample.tex`. MiKTeX will try to download the missing packages off the internet. You might have to install them manually in TexLive by using tlmgr.
3. See `LatexTemplateIT`/`SynchronousLatexRendererIT` to get started in your project.

# Server Tasks
Install missing TexLive packages
1. change to user latex
2. run `tlmgr init-usertree`, if this is the first time
3. run `tlmgr install <package>` to install the missing package

# Usage
- Generating a pdf: send post request with tex body to base url. The response will contain a uuid, which is used to retrieve the pdf. Status code will be accepted(202).
- Retrieving a pdf: send get request to base url, e.g. http://renderer/65891272-fbd6-44fe-8152-dc4ea8e1901b. If it's not available yet, the service will return a 404 status code. If it was already created, the pdf file will be returned.
- The server status url is /admin/index.html. Jolokia(jmx remoting) and dropwizard metrics have been integrated.

# Scaling the service
- Deploy it in a loadbalanced setup on multiple servers
- Our installation is using [GlusterFS](https://www.gluster.org/) as backend storage of the archive folder, so the pdfs will be distributed and available on all members of the cluster. Each job output is stored in a subdirectory named by its uuid. The tex template and the pdf will be stored there for debugging purposes.
- LaTeX is single threaded, so the render jobs are submitted to a thread pool, which is sized to use the number of physical cpus as a limit to parallel threads (default value).

# ToDos/Limitations
- The whole tex template is submitted in the post request, so it's currently not possible to include external resources (e.g. images), because those won't be found/available on the render server. Use [pdfinlimg](https://github.com/zerotoc/pdfinlimg) to inline small images (e.g. company logos) and supply them within the tex document. 
- Currenly pdflatex is run multiple times to create the pdf. BibTeX or other external tools aren't executed as part of this build. If you need that, it's probably easier to switch to [Latexmk](http://personal.psu.edu/jcc8/latexmk/) instead of pdflatex to render files

