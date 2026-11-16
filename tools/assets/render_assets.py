#!/usr/bin/env python3
import os
import sys
import cairosvg
from PIL import Image

def generate_kidney_svg():
    # Size: 448 x 628 px (112 x 157 dp at 4x)
    # Origin: top-left of kidney group box
    # Body: spans x ~80 to 448 px, y ~0 to 540 px
    # Ureter: spans x ~0 to 140 px, y ~300 to 584 px
    # Vein & Artery stubs: leave notch to left at y ~220 to 280 px
    svg = """<svg xmlns="http://www.w3.org/2000/svg" width="448" height="628" viewBox="0 0 448 628">
  <defs>
    <!-- Kidney body 3D radial gradient -->
    <radialGradient id="kidneyBodyGrad" cx="38%" cy="30%" r="65%" fx="35%" fy="25%">
      <stop offset="0%" stop-color="#E8A8AB" />
      <stop offset="25%" stop-color="#D99C9F" />
      <stop offset="55%" stop-color="#AE5A5D" />
      <stop offset="80%" stop-color="#8F3C42" />
      <stop offset="95%" stop-color="#7D343B" />
      <stop offset="100%" stop-color="#782D32" />
    </radialGradient>

    <!-- Top-left soft highlight glow -->
    <radialGradient id="softHighlight" cx="35%" cy="28%" r="40%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.45" />
      <stop offset="50%" stop-color="#F2C2C5" stop-opacity="0.2" />
      <stop offset="100%" stop-color="#AE5A5D" stop-opacity="0" />
    </radialGradient>

    <!-- Ureter gradient -->
    <linearGradient id="ureterGrad" x1="40%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#F4CDB9" />
      <stop offset="50%" stop-color="#EBB69D" />
      <stop offset="100%" stop-color="#D5957A" />
    </linearGradient>

    <!-- Artery (red) -->
    <linearGradient id="arteryGrad" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#D46A72" />
      <stop offset="100%" stop-color="#B2464E" />
    </linearGradient>

    <!-- Vein (blue) -->
    <linearGradient id="veinGrad" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#90B5E6" />
      <stop offset="100%" stop-color="#678EBF" />
    </linearGradient>

    <!-- Hilum occlusion shadow -->
    <radialGradient id="hilumShadow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#4A181D" stop-opacity="0.6" />
      <stop offset="100%" stop-color="#7D343B" stop-opacity="0" />
    </radialGradient>

    <filter id="softGlow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="8" />
    </filter>
  </defs>

  <!-- 1. Stubs at hilum (artery red & vein blue) -->
  <!-- Vein stub (blue, top) -->
  <g>
    <path d="M 100 236 L 155 236 C 165 236 175 240 180 248 L 180 268 C 175 272 165 274 155 274 L 100 274 C 92 274 86 265 86 255 C 86 245 92 236 100 236 Z" 
          fill="url(#veinGrad)" />
    <!-- vein inner light highlight -->
    <path d="M 100 240 L 155 240 C 162 240 170 243 174 248" 
          stroke="#C5DBF8" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.6" />
  </g>

  <!-- Artery stub (red, below vein) -->
  <g>
    <path d="M 70 270 L 140 270 C 150 270 160 274 165 282 L 165 304 C 155 308 145 310 135 310 L 70 310 C 62 310 56 301 56 290 C 56 279 62 270 70 270 Z" 
          fill="url(#arteryGrad)" />
    <path d="M 70 275 L 135 275 C 142 275 150 278 154 284" 
          stroke="#F8B6BC" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.6" />
  </g>

  <!-- 2. Ureter curling down and left from hilum -->
  <g>
    <!-- Shadow behind ureter -->
    <path d="M 180 340 C 150 370 110 430 80 500 C 65 535 50 580 40 615 C 36 628 50 632 58 622 C 72 582 92 535 116 488 C 144 430 185 375 210 350 Z" 
          fill="#4A181D" opacity="0.25" filter="url(#softGlow)" />
    <!-- Ureter body -->
    <path d="M 175 330 C 145 365 105 425 75 495 C 60 530 45 575 35 610 C 33 618 42 624 49 618 C 63 578 83 530 108 482 C 138 425 180 370 205 342 Z" 
          fill="url(#ureterGrad)" />
    <!-- Specular highlight streak along ureter curve -->
    <path d="M 170 338 C 142 372 104 430 76 498 C 63 528 50 568 42 602" 
          stroke="#FFF2EB" stroke-width="5" stroke-linecap="round" fill="none" opacity="0.75" />
  </g>

  <!-- 3. Kidney main clay body -->
  <!-- Classic kidney bean shape with hilum notch at left -->
  <path id="kidneyBody" d="
    M 260 20
    C 360 20, 440 100, 440 220
    C 440 350, 400 480, 310 530
    C 240 570, 180 540, 150 490
    C 125 450, 120 400, 130 360
    C 145 310, 165 285, 160 250
    C 152 210, 115 170, 130 110
    C 150 45, 200 20, 260 20 Z"
    fill="url(#kidneyBodyGrad)" />

  <!-- 4. Soft hilum occlusion shadow in the notch -->
  <path d="
    M 130 230
    C 150 250, 175 280, 160 320
    C 145 350, 135 380, 140 400
    C 120 370, 110 320, 120 270 Z"
    fill="url(#hilumShadow)" opacity="0.6" filter="url(#softGlow)" />

  <!-- 5. Volumetric 3D top-left light highlight (diffuse sheen) -->
  <ellipse cx="270" cy="150" rx="130" ry="100" fill="url(#softHighlight)" transform="rotate(-15 270 150)" />

  <!-- 6. Secondary soft rim highlight along top curve -->
  <path d="M 200 35 C 250 26 330 32 390 90 C 420 125 432 170 430 220"
        stroke="#FFFFFF" stroke-width="12" stroke-linecap="round" fill="none" opacity="0.25" filter="url(#softGlow)" />
  <path d="M 220 32 C 265 26 335 34 385 85"
        stroke="#FFFFFF" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.5" />
</svg>
"""
    return svg

def generate_pipe_svg(is_left=True):
    # Size: 592 x 516 px (148 x 129 dp at 4x)
    # Tube diameter: 100 px (25 dp at 4x)
    # Radius of bend: 160 px (40 dp at 4x)
    # 24 dp padding (96 px) around for glow
    
    if is_left:
        # Left cyan pipe:
        # Comes from bottom-left (vial top), goes up, bends 90 deg right towards kidney
        core_col = "#8BE7F0"
        rim_col = "#7FDAE6"
        hi_col = "#D8F6FA"
        glow_col = "#BEEAF1"
        collar_body = "#CDEEF4"
        collar_rim = "#FFFFFF"
        # Tube path:
        # Bottom start at x=112 px (aligned with vial collar), runs up to bend y=210 px
        # Bends right to horizontal leg y=110 px, runs right to kidney collar at x=496 px
        svg = f"""<svg xmlns="http://www.w3.org/2000/svg" width="592" height="516" viewBox="0 0 592 516">
  <defs>
    <filter id="pipeGlow" x="-30%" y="-30%" width="160%" height="160%">
      <feGaussianBlur stdDeviation="28" />
    </filter>
    <filter id="softBlur" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="6" />
    </filter>
    
    <!-- Vertical pipe cross-section gradient -->
    <linearGradient id="tubeHGrad" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="{rim_col}" stop-opacity="0.9" />
      <stop offset="25%" stop-color="{hi_col}" stop-opacity="0.95" />
      <stop offset="55%" stop-color="{core_col}" stop-opacity="0.85" />
      <stop offset="85%" stop-color="{rim_col}" stop-opacity="0.9" />
      <stop offset="100%" stop-color="#4FA8CC" stop-opacity="0.75" />
    </linearGradient>

    <linearGradient id="collarGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.95" />
      <stop offset="40%" stop-color="{collar_body}" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#A6DDE6" stop-opacity="0.7" />
    </linearGradient>
  </defs>

  <!-- 1. Ambient pipe glow onto card surface behind -->
  <path d="M 112 430 L 112 250 A 140 140 0 0 1 252 110 L 496 110" 
        stroke="{glow_col}" stroke-width="150" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.55" filter="url(#pipeGlow)" />

  <!-- 2. Pipe translucent body -->
  <path d="M 112 430 L 112 250 A 140 140 0 0 1 252 110 L 496 110" 
        stroke="{core_col}" stroke-width="96" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.82" />

  <!-- 3. Inner core / refraction -->
  <path d="M 112 430 L 112 250 A 140 140 0 0 1 252 110 L 496 110" 
        stroke="url(#tubeHGrad)" stroke-width="84" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 4. Specular streak along upper / inner curvature -->
  <path d="M 88 430 L 88 250 A 164 164 0 0 1 252 86 L 496 86" 
        stroke="#FFFFFF" stroke-width="12" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.7" filter="url(#softBlur)" />
  <path d="M 88 430 L 88 250 A 164 164 0 0 1 252 86 L 496 86" 
        stroke="#FFFFFF" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 5. Collars -->
  <!-- Vial collar (horizontal ring at bottom end: 31 x 16 dp = 124 x 64 px) -->
  <g transform="translate(50, 398)">
    <rect x="0" y="0" width="124" height="64" rx="20" fill="url(#collarGrad)" />
    <rect x="1" y="1" width="122" height="62" rx="19" stroke="#FFFFFF" stroke-width="4" fill="none" opacity="0.85" />
  </g>

  <!-- Kidney collar (vertical ring at kidney end: 15 x 31 dp = 60 x 124 px) -->
  <g transform="translate(466, 48)">
    <rect x="0" y="0" width="60" height="124" rx="20" fill="url(#collarGrad)" />
    <rect x="1" y="1" width="58" height="122" rx="19" stroke="#FFFFFF" stroke-width="4" fill="none" opacity="0.85" />
  </g>
</svg>"""
        return svg
    else:
        # Right amber pipe:
        # Comes from bottom-right (vial top), goes up, bends 90 deg left towards kidney
        core_col = "#E9CE97"
        rim_col = "#D9BA7E"
        hi_col = "#F1D69C"
        glow_col = "#E4D1B0"
        collar_body = "#F2E2C4"
        
        # Mirrored horizontally:
        # Right vial end at x = 592 - 112 = 480 px.
        # Bends left to horizontal leg y=110 px, runs left to kidney collar at x = 592 - 496 = 96 px.
        svg = f"""<svg xmlns="http://www.w3.org/2000/svg" width="592" height="516" viewBox="0 0 592 516">
  <defs>
    <filter id="pipeGlowAmber" x="-30%" y="-30%" width="160%" height="160%">
      <feGaussianBlur stdDeviation="28" />
    </filter>
    <filter id="softBlurAmber" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="6" />
    </filter>
    
    <linearGradient id="tubeHGradAmber" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="{rim_col}" stop-opacity="0.9" />
      <stop offset="25%" stop-color="{hi_col}" stop-opacity="0.95" />
      <stop offset="55%" stop-color="{core_col}" stop-opacity="0.85" />
      <stop offset="85%" stop-color="{rim_col}" stop-opacity="0.9" />
      <stop offset="100%" stop-color="#C99B4A" stop-opacity="0.75" />
    </linearGradient>

    <linearGradient id="collarGradAmber" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.95" />
      <stop offset="40%" stop-color="{collar_body}" stop-opacity="0.85" />
      <stop offset="100%" stop-color="#D9BA7E" stop-opacity="0.7" />
    </linearGradient>
  </defs>

  <!-- 1. Ambient pipe glow -->
  <path d="M 480 430 L 480 250 A 140 140 0 0 0 340 110 L 96 110" 
        stroke="{glow_col}" stroke-width="150" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.55" filter="url(#pipeGlowAmber)" />

  <!-- 2. Pipe body -->
  <path d="M 480 430 L 480 250 A 140 140 0 0 0 340 110 L 96 110" 
        stroke="{core_col}" stroke-width="96" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.82" />

  <!-- 3. Inner refraction -->
  <path d="M 480 430 L 480 250 A 140 140 0 0 0 340 110 L 96 110" 
        stroke="url(#tubeHGradAmber)" stroke-width="84" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 4. Specular highlight streak along upper / inner curve -->
  <path d="M 504 430 L 504 250 A 164 164 0 0 0 340 86 L 96 86" 
        stroke="#FFFFFF" stroke-width="12" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.7" filter="url(#softBlurAmber)" />
  <path d="M 504 430 L 504 250 A 164 164 0 0 0 340 86 L 96 86" 
        stroke="#FFFFFF" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.9" />

  <!-- 5. Collars -->
  <!-- Vial collar (horizontal ring at bottom end: 124 x 64 px) -->
  <g transform="translate(418, 398)">
    <rect x="0" y="0" width="124" height="64" rx="20" fill="url(#collarGradAmber)" />
    <rect x="1" y="1" width="122" height="62" rx="19" stroke="#FFFFFF" stroke-width="4" fill="none" opacity="0.85" />
  </g>

  <!-- Kidney collar (vertical ring at kidney end: 60 x 124 px) -->
  <g transform="translate(66, 48)">
    <rect x="0" y="0" width="60" height="124" rx="20" fill="url(#collarGradAmber)" />
    <rect x="1" y="1" width="58" height="122" rx="19" stroke="#FFFFFF" stroke-width="4" fill="none" opacity="0.85" />
  </g>
</svg>"""
        return svg

def main():
    os.makedirs("tools/assets", exist_ok=True)
    os.makedirs("app/src/main/res/drawable-nodpi", exist_ok=True)
    
    # Write SVGs
    kidney_svg_path = "tools/assets/kidney_3d.svg"
    pipe_left_svg_path = "tools/assets/pipe_blue_left.svg"
    pipe_right_svg_path = "tools/assets/pipe_amber_right.svg"
    
    with open(kidney_svg_path, "w") as f:
        f.write(generate_kidney_svg())
    with open(pipe_left_svg_path, "w") as f:
        f.write(generate_pipe_svg(is_left=True))
    with open(pipe_right_svg_path, "w") as f:
        f.write(generate_pipe_svg(is_left=False))
        
    print("Layered SVGs saved in tools/assets/")
    
    # Rasterize to PNGs in app/src/main/res/drawable-nodpi/
    cairosvg.svg2png(url=kidney_svg_path, write_to="app/src/main/res/drawable-nodpi/kidney_3d.png", output_width=448, output_height=628)
    cairosvg.svg2png(url=pipe_left_svg_path, write_to="app/src/main/res/drawable-nodpi/pipe_blue_left.png", output_width=592, output_height=516)
    cairosvg.svg2png(url=pipe_right_svg_path, write_to="app/src/main/res/drawable-nodpi/pipe_amber_right.png", output_width=592, output_height=516)
    
    print("Rasterized PNGs saved in app/src/main/res/drawable-nodpi/:")
    for f in ["kidney_3d.png", "pipe_blue_left.png", "pipe_amber_right.png"]:
        path = os.path.join("app/src/main/res/drawable-nodpi", f)
        im = Image.open(path)
        sz_kb = os.path.getsize(path) / 1024.0
        print(f" - {f}: {im.size}, {sz_kb:.1f} KB")

if __name__ == "__main__":
    main()
