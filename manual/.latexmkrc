$pdf_mode = 1;
@default_files = ("main");
$jobname = "manual";


# Check to ensure PlantUML is available.
unless (-e 'plantuml.jar') {
    print <<'END_MESSAGE';
PlantUML not found.
Ensure plantuml.jar is located in the manual subdirectory.
See https://plantuml.com.
END_MESSAGE
    exit 2;
}


ensure_path('TEXINPUTS', './figures/plantuml//');


# Create a custom dependency to generate images from PlantUML.
add_cus_dep('puml', 'pdf_tex', 1, 'convert_plantuml');


# Converts a PlantUML diagram into an image suitable for inclusion into
# the final PDF output.
sub convert_plantuml {
    my $do_plantuml = 1;

    # Check to see if the source file contains the SKIP_PLANTUML keyword.
    # This is intended for figures that must be manually adjusted with
    # Inkscape before inclusion into the document, so PlantUML is not
    # executed automatically but Inkscape will still be run to convert
    # the SVG into PDF/LaTeX.
    open(FH, "$_[0].puml") or die("File $_[0].puml not found");
    while (my $line = <FH>) {
        if ($line =~ /SKIP_PLANTUML/) {
            $do_plantuml = 0;
        }
    }
    close(FH);

    # Run PlantUML to generate an SVG graphic file.
    if ($do_plantuml) {
        my @plantuml_args = ("java",
                             "-jar",
                             "plantuml.jar",
                             "-tsvg",
                             "$_[0].puml");
        system(@plantuml_args);
    }

    # Use Inkscape to convert the SVG into PDF images plus LaTeX text.
    if (!$do_plantuml or ($? == 0)) {
        my @inkscape_args = ("inkscape",
                             "--export-area-drawing",
                             "--without-gui",
                             "--file=$_[0].svg",
                             "--export-pdf=$_[0].pdf",
                             "--export-latex");
        system(@inkscape_args);
    }

    return $?
}
