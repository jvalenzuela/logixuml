$pdf_mode = 1;
@default_files = ("main");
$jobname = "manual";


ensure_path('TEXINPUTS', './figures/plantuml//');


# Create a custom dependency to generate images from PlantUML.
add_cus_dep('puml', 'pdf_tex', 1, 'convert_plantuml');


# Converts a PlantUML diagram into an image suitable for inclusion into
# the final PDF output.
sub convert_plantuml {
    # Run PlantUML to generate an SVG graphic file.
    my @plantuml_args = ("java",
                         "-jar",
                         "plantuml.jar",
                         "-tsvg",
                         "\"$_[0].puml\"");
    system(@plantuml_args);

    # Use Inkscape to convert the SVG into PDF images plus LaTeX text.
    if ($? == 0) {
        my @inkscape_args = ("inkscape",
                             "--export-area-drawing",
                             "--without-gui",
                             "--file=\"$_[0].svg\"",
                             "--export-pdf=\"$_[0].pdf\"",
                             "--export-latex");
        system(@inkscape_args);
    }

    return $?
}
