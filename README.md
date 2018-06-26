[![Build Status](https://travis-ci.org/vsfexperts/LaTeX.svg)](https://travis-ci.org/vsfexperts/LaTeX)
[![codecov](https://codecov.io/gh/vsfexperts/LaTeX/branch/development/graph/badge.svg)](https://codecov.io/gh/vsfexperts/LaTeX)

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
2. Browse to `latex-renderer/src/test/resources/META-INF` and compile the `sample.tex` file by executing `pdflatex sample.tex`. MiKTeX will try to download the missing packages off the internet. You might have to install them manually in TexLive by using tlmgr.
3. See [LatexTemplateIT](latex-template/src/test/java/de/vsfexperts/latex/template/LatexTemplateIT.java) or [SynchronousLatexRendererIT](latex-renderer/src/test/java/de/vsfexperts/latex/renderer/SynchronousLatexRendererIT.java) to get started in your project.

# Server Tasks
Install missing TexLive packages
1. change to user latex
2. run `tlmgr init-usertree`, if this is the first time
3. run `tlmgr install <package>` to install the missing package

# Usage
- Generating a pdf: send post request with tex body to base url. The response will contain a uuid, which is used to retrieve the pdf. Status code will be accepted(202).
- Retrieving a pdf: send get request to base url, e.g. http://renderer/65891272-fbd6-44fe-8152-dc4ea8e1901b. If it's not available yet, the service will return a 404 status code. If it was already created, the pdf file will be returned.
- The server status url is /admin/index.html. Jolokia(jmx remoting) and micrometer metrics have been integrated.

# Configuration
There're 3 config values, which can be set in [application.yaml](latex-server/src/main/resources/application.yaml) or by specifying them on the command line:

| Property                  |  Default (prod)    | Description                                                           |
| ------------------------- | ------------------ | --------------------------------------------------------------------- | 
| renderer.archiveDirectory | /var/archive       | Base directory of archive. Render output will be stored in subfolders | 
| renderer.workDirectory    | /tmp/renderer      | Base temp directory. Temp data will be stored in subfolders           |
| renderer.threads          | \<number of cpus\> | Size of rendering thread pool                                         | 

example: `java -Dspring.profiles.active=prod -Drenderer.threads=2 -Drenderer.workDirectory=c:\temp\work -Drenderer.archiveDirectory=c:\temp\archive -jar latex-server.jar`

# Scaling the service
- Deployment of multiple instances of the renderer in a loadbalanced setup on multiple servers (round robin/least load preferred)
- Security setup (authentication/authorization) should be configured in load balancer as well (e.g. basic auth in nginx in most simple cases). It's not part of this project.
- LaTeX is single threaded, so the render jobs are submitted to a thread pool, which is sized to use the number of physical cpus as a limit to parallel threads (default value). The queue size of the pool is unlimited atm, so that's the reason to prefer round robin in order to reduce the risk of overloading a node. The queued render jobs aren't stored on disk (only in memory), so those are going to be lost if the node has crashed/ is stopped.
- Most of the load is created in the rendering process, so the scaling of the service is pretty easy by adding new render nodes. The archive directory should be kept on a distributed filesystem, so that each node is able to return the results to the client. Our installation is using [GlusterFS](https://www.gluster.org/) as backend storage of the archive folder.
- Each job is receiving a new uuid, so the output in the archive is immutable. It's possible to cache the job output as a result in some frontend cache (e.g. Varnish). This might be necessary if there're a lot of reads of the generated output.

# ToDos/Limitations
- The whole tex template is submitted in the post request, so it's currently not possible to include external resources (e.g. images), because those won't be found/available on the render server. Use [pdfinlimg](https://github.com/zerotoc/pdfinlimg) to inline small images (e.g. company logos) and supply them within the tex document. 
- Currenly pdflatex is run multiple times to create the pdf. BibTeX or other external tools aren't executed as part of this build. If you need that, it's probably easier to switch to [Latexmk](http://personal.psu.edu/jcc8/latexmk/) instead of pdflatex to render files

