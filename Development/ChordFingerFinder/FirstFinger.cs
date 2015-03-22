using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CFstFinger : CBasicGuitarFinger
    {
        public CFstFinger(CGuitarFinger FirstFinger)
            : base(FirstFinger)
        {

        }

        public CFstFinger(List<CGuitarFinger> FirstFinger)
            : base(FirstFinger)
        {

        }

        public CFstFinger()
            : base()
        {

        }

        static public int CompareFingers(CFstFinger fst, CFstFinger sec)
        {
            if (sec.Fret == fst.Fret)
            {
                return sec.Priority - fst.Priority;
            }

            return fst.Fret - sec.Fret;
        }

        public override int GetIndex()
        {
            return 1;
        }

        protected override bool GetPriorityForPlay(CFingerPosition cFngr, bool fCapo,out int nPriority)
        {
            nPriority = 0;

            if (!cFngr.IsValid())
            {
                return false;
                //throw new Exception("Fuck it");
            }

            int nResult = 0;
            nResult += Globals.NUMBER_OF_FRETS - cFngr.Fret;

            nResult += ((EString.NumberOfStrings - cFngr.String) * Globals.NUMBER_OF_FRETS_IN_OCTAVE / 3); // TODO: This should be more dynamic.

            if (fCapo)
            {
                nResult += (Strings.Count / 2);
            }

            nPriority = nResult;
            return true;
        }


        public override bool CanPlay(CGuitarFinger cFngr)
        {
            if (cFngr == null)
            {
                return false;
            }

            return base.CanPlay(cFngr);
        }
    }
}
