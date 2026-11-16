#!/usr/bin/env python3
"""
Export 5x lossless assets for TrackWell Home Screen
Section 12 of Android Home Screen Fix and Responsive Prompt:
- kidney_3d.png: 560 x 785 px (112 x 157 dp)
- pipe_blue_left.png: 775 x 595 px (155 x 119 dp)
- pipe_amber_right.png: 775 x 595 px (155 x 119 dp)
All 4 corners of all 3 images MUST have alpha 0.
"""

import os
import cairosvg
from PIL import Image

def get_kidney_svg():
    return '''<svg xmlns="http://www.w3.org/2000/svg" width="560" height="785" viewBox="0 0 560 785">
  <defs>
    <!-- Kidney body 3D radial gradient -->
    <radialGradient id="kidneyBodyGrad" cx="40%" cy="30%" r="62%" fx="36%" fy="24%">
      <stop offset="0%" stop-color="#E8A8AB" />
      <stop offset="20%" stop-color="#D99C9F" />
      <stop offset="50%" stop-color="#AE5A5D" />
      <stop offset="75%" stop-color="#A04E52" />
      <stop offset="90%" stop-color="#7D343B" />
      <stop offset="100%" stop-color="#782D32" />
    </radialGradient>

    <!-- Top-left soft highlight glow -->
    <radialGradient id="softHighlight" cx="36%" cy="28%" r="38%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.4" />
      <stop offset="45%" stop-color="#F2C2C5" stop-opacity="0.15" />
      <stop offset="100%" stop-color="#AE5A5D" stop-opacity="0" />
    </radialGradient>

    <!-- Ureter gradient: #F0C3AE to #DFA58E -->
    <linearGradient id="ureterGrad" x1="40%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#F0C3AE" />
      <stop offset="50%" stop-color="#E8B39B" />
      <stop offset="100%" stop-color="#DFA58E" />
    </linearGradient>

    <!-- Artery (red: #C45A62 to #A83E46) -->
    <linearGradient id="arteryGrad" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#D46A72" />
      <stop offset="100%" stop-color="#C45A62" />
    </linearGradient>

    <!-- Vein (blue: #7FA5D6 to #5F85B6) -->
    <linearGradient id="veinGrad" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#90B5E6" />
      <stop offset="100%" stop-color="#7FA5D6" />
    </linearGradient>

    <!-- Hilum occlusion shadow -->
    <radialGradient id="hilumShadow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#4A181D" stop-opacity="0.6" />
      <stop offset="100%" stop-color="#7D343B" stop-opacity="0" />
    </radialGradient>

    <filter id="softHilum" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="6" />
    </filter>
    <filter id="subtleGleam" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="8" />
    </filter>
  </defs>

  <!-- 1. Stubs at hilum (artery red & vein blue) at y 56-71 dp = 280-355 px -->
  <!-- Vein stub (blue, top: y ~295 to 330 px) -->
  <g>
    <path d="M 105 295 L 180 295 C 190 295 200 300 205 310 L 205 330 C 200 335 190 338 180 338 L 105 338 C 95 338 88 328 88 316 C 88 304 95 295 105 295 Z" 
          fill="url(#veinGrad)" />
    <path d="M 105 300 L 180 300 C 188 300 196 304 200 310" 
          stroke="#C5DBF8" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.6" />
  </g>

  <!-- Artery stub (red, below vein: y ~335 to 375 px) -->
  <g>
    <path d="M 68 335 L 160 335 C 170 335 180 340 185 350 L 185 372 C 175 376 165 378 155 378 L 68 378 C 58 378 50 368 50 356 C 50 344 58 335 68 335 Z" 
          fill="url(#arteryGrad)" />
    <path d="M 68 340 L 155 340 C 162 340 170 344 175 350" 
          stroke="#F8B6BC" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.6" />
  </g>

  <!-- 2. Ureter curling down and left from hilum (y ~410 to 740 px) -->
  <g>
    <!-- Ureter shadow inside hilum fold -->
    <path d="M 220 420 C 185 460 135 530 100 610 C 80 650 62 700 50 735 C 46 746 58 750 67 740 C 82 695 105 640 135 585 C 170 515 220 455 250 425 Z" 
          fill="#4A181D" opacity="0.2" />
    <!-- Ureter clay body -->
    <path d="M 215 410 C 180 450 130 520 95 605 C 75 645 58 695 45 735 C 42 743 52 748 60 742 C 75 698 98 642 128 588 C 162 520 212 458 245 422 Z" 
          fill="url(#ureterGrad)" />
    <!-- Specular highlight streak along ureter curve -->
    <path d="M 210 418 C 176 458 128 528 95 610 C 78 645 62 690 52 728" 
          stroke="#FFF2EB" stroke-width="6" stroke-linecap="round" fill="none" opacity="0.8" />
  </g>

  <!-- 3. Kidney main organic clay body (uncut dome and lower lobe) -->
  <path id="kidneyBody" d="
    M 315 28
    C 435 28, 532 120, 532 265
    C 532 420, 485 575, 375 635
    C 290 682, 220 648, 180 588
    C 150 540, 145 480, 156 430
    C 174 370, 198 340, 192 300
    C 182 250, 140 205, 158 135
    C 182 58, 242 28, 315 28 Z"
    fill="url(#kidneyBodyGrad)" />

  <!-- 4. Soft hilum occlusion shadow in the notch -->
  <path d="
    M 156 275
    C 180 300, 210 335, 192 385
    C 174 420, 162 455, 168 480
    C 144 445, 132 385, 144 325 Z"
    fill="url(#hilumShadow)" opacity="0.55" filter="url(#softHilum)" />

  <!-- 5. Volumetric 3D top-left light highlight (diffuse sheen) -->
  <ellipse cx="325" cy="180" rx="155" ry="120" fill="url(#softHighlight)" transform="rotate(-15 325 180)" />

  <!-- 6. Secondary soft rim highlight along top curve -->
  <path d="M 240 45 C 300 35 400 42 470 110 C 510 150 522 205 520 265"
        stroke="#FFFFFF" stroke-width="14" stroke-linecap="round" fill="none" opacity="0.2" filter="url(#subtleGleam)" />
  <path d="M 260 42 C 320 35 405 45 465 105"
        stroke="#FFFFFF" stroke-width="5" stroke-linecap="round" fill="none" opacity="0.5" />
</svg>'''

def get_left_pipe_svg():
    return '''<svg xmlns="http://www.w3.org/2000/svg" width="775" height="595" viewBox="0 0 775 595">
  <defs>
    <filter id="pipeGlow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="20" />
    </filter>
    <filter id="softBlur" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="5" />
    </filter>

    <linearGradient id="tubeGrad" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#7FDAE6" stop-opacity="0.85" />
      <stop offset="20%" stop-color="#D8F6FA" stop-opacity="0.95" />
      <stop offset="50%" stop-color="#8BE7F0" stop-opacity="0.8" />
      <stop offset="80%" stop-color="#7FDAE6" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#52ACCB" stop-opacity="0.75" />
    </linearGradient>

    <linearGradient id="collarGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.95" />
      <stop offset="35%" stop-color="#CDEEF4" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#95DCE8" stop-opacity="0.75" />
    </linearGradient>
  </defs>

  <!-- 1. Soft cyan ambient glow behind tube -->
  <path d="M 160 475 L 160 360 A 200 200 0 0 1 360 160 L 655 160" 
        stroke="#BEEAF1" stroke-width="165" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.5" filter="url(#pipeGlow)" />

  <!-- 2. Glass tube outer translucent sheath -->
  <path d="M 160 475 L 160 360 A 200 200 0 0 1 360 160 L 655 160" 
        stroke="#7FDAE6" stroke-width="125" stroke-linecap="butt" stroke-linejoin="round" fill="none" opacity="0.4" />

  <!-- 3. Glass tube core -->
  <path d="M 160 475 L 160 360 A 200 200 0 0 1 360 160 L 655 160" 
        stroke="url(#tubeGrad)" stroke-width="110" stroke-linecap="butt" stroke-linejoin="round" fill="none" opacity="0.85" />

  <!-- 4. Specular spine highlight along outer curvature -->
  <path d="M 132 475 L 132 360 A 228 228 0 0 1 360 132 L 655 132" 
        stroke="#FFFFFF" stroke-width="14" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.7" filter="url(#softBlur)" />
  <path d="M 132 475 L 132 360 A 228 228 0 0 1 360 132 L 655 132" 
        stroke="#FFFFFF" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 5. Collars -->
  <!-- Vial collar (horizontal pill at 160, 475: 155 x 80 px) -->
  <g>
    <rect x="82.5" y="435" width="155" height="80" rx="28" ry="28" fill="url(#collarGrad)" />
    <rect x="85" y="437.5" width="150" height="75" rx="26" ry="26" stroke="#FFFFFF" stroke-width="5" fill="none" opacity="0.85" />
  </g>

  <!-- Kidney collar (vertical pill at 655, 160: 75 x 155 px) -->
  <g>
    <rect x="617.5" y="82.5" width="75" height="155" rx="28" ry="28" fill="url(#collarGrad)" />
    <rect x="620" y="85" width="70" height="150" rx="26" ry="26" stroke="#FFFFFF" stroke-width="5" fill="none" opacity="0.85" />
  </g>
</svg>'''

def get_right_pipe_svg():
    return '''<svg xmlns="http://www.w3.org/2000/svg" width="775" height="595" viewBox="0 0 775 595">
  <defs>
    <filter id="pipeGlowAmber" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="20" />
    </filter>
    <filter id="softBlurAmber" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="5" />
    </filter>

    <linearGradient id="tubeGradAmber" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#D9BA7E" stop-opacity="0.85" />
      <stop offset="20%" stop-color="#F1D69C" stop-opacity="0.95" />
      <stop offset="50%" stop-color="#E9CE97" stop-opacity="0.8" />
      <stop offset="80%" stop-color="#D9BA7E" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#C99B4A" stop-opacity="0.75" />
    </linearGradient>

    <linearGradient id="collarGradAmber" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.95" />
      <stop offset="35%" stop-color="#F2E2C4" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#D9BA7E" stop-opacity="0.75" />
    </linearGradient>
  </defs>

  <!-- 1. Soft amber ambient glow behind tube -->
  <path d="M 615 475 L 615 355 A 200 200 0 0 0 415 155 L 130 155" 
        stroke="#E4D1B0" stroke-width="165" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.5" filter="url(#pipeGlowAmber)" />

  <!-- 2. Glass tube outer translucent sheath -->
  <path d="M 615 475 L 615 355 A 200 200 0 0 0 415 155 L 130 155" 
        stroke="#D9BA7E" stroke-width="125" stroke-linecap="butt" stroke-linejoin="round" fill="none" opacity="0.4" />

  <!-- 3. Glass tube core -->
  <path d="M 615 475 L 615 355 A 200 200 0 0 0 415 155 L 130 155" 
        stroke="url(#tubeGradAmber)" stroke-width="110" stroke-linecap="butt" stroke-linejoin="round" fill="none" opacity="0.85" />

  <!-- 4. Specular spine highlight along outer curvature -->
  <path d="M 643 475 L 643 355 A 228 228 0 0 0 415 127 L 130 127" 
        stroke="#FFFFFF" stroke-width="14" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.7" filter="url(#softBlurAmber)" />
  <path d="M 643 475 L 643 355 A 228 228 0 0 0 415 127 L 130 127" 
        stroke="#FFFFFF" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 5. Collars -->
  <!-- Vial collar (horizontal pill at 615, 475: 155 x 80 px) -->
  <g>
    <rect x="537.5" y="435" width="155" height="80" rx="28" ry="28" fill="url(#collarGradAmber)" />
    <rect x="540" y="437.5" width="150" height="75" rx="26" ry="26" stroke="#FFFFFF" stroke-width="5" fill="none" opacity="0.85" />
  </g>

  <!-- Kidney collar (vertical pill at 130, 155: 75 x 155 px) -->
  <g>
    <rect x="92.5" y="77.5" width="75" height="155" rx="28" ry="28" fill="url(#collarGradAmber)" />
    <rect x="95" y="80" width="70" height="150" rx="26" ry="26" stroke="#FFFFFF" stroke-width="5" fill="none" opacity="0.85" />
  </g>
</svg>'''

def main():
    assets_dir = "tools/assets"
    res_dir = "app/src/main/res/drawable-nodpi"
    os.makedirs(assets_dir, exist_ok=True)
    os.makedirs(res_dir, exist_ok=True)

    # Save SVGs
    kidney_svg_path = os.path.join(assets_dir, "kidney_3d.svg")
    pipe_left_svg_path = os.path.join(assets_dir, "pipe_blue_left.svg")
    pipe_right_svg_path = os.path.join(assets_dir, "pipe_amber_right.svg")

    with open(kidney_svg_path, "w") as f:
        f.write(get_kidney_svg())
    with open(pipe_left_svg_path, "w") as f:
        f.write(get_left_pipe_svg())
    with open(pipe_right_svg_path, "w") as f:
        f.write(get_right_pipe_svg())

    print("Saved SVGs in", assets_dir)

    # Rasterize 5x PNGs
    kidney_png_path = os.path.join(res_dir, "kidney_3d.png")
    pipe_left_png_path = os.path.join(res_dir, "pipe_blue_left.png")
    pipe_right_png_path = os.path.join(res_dir, "pipe_amber_right.png")

    cairosvg.svg2png(url=kidney_svg_path, write_to=kidney_png_path, output_width=560, output_height=785)
    cairosvg.svg2png(url=pipe_left_svg_path, write_to=pipe_left_png_path, output_width=775, output_height=595)
    cairosvg.svg2png(url=pipe_right_svg_path, write_to=pipe_right_png_path, output_width=775, output_height=595)

    print("Rasterized 5x PNGs in", res_dir)

    # Verification of corner alpha
    for name in ["kidney_3d", "pipe_blue_left", "pipe_amber_right"]:
        path = os.path.join(res_dir, f"{name}.png")
        im = Image.open(path).convert("RGBA")
        a = im.getchannel("A")
        w, h = im.size
        corners = [a.getpixel(p) for p in [(0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)]]
        print(f"{name}: size={im.size}, alpha extrema={a.getextrema()}, corner alpha={corners}")

if __name__ == "__main__":
    main()
